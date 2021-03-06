package org.jocean.sts.job;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.zookeeper.CreateMode;
import org.jocean.aliyun.ecs.MetadataAPI;
import org.jocean.aliyun.sts.STSCredentials;
import org.jocean.idiom.BeanHolder;
import org.jocean.idiom.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

@Component("updatestsc")
class UpdateSTSCJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSTSCJob.class);

    void update() {
        final String stscId = _instanceId + "-stsc";

        final STSCredentials stsc = beanHolder.getBean(stscId, STSCredentials.class);

        final MetadataAPI.STSTokenBuilder getststoken = beanHolder.getBean(MetadataAPI.STSTokenBuilder.class);

        LOG.info("update by {}: getststoken {}, zkconn info {}, ecs instance {}/stsc:{}",
                this, getststoken, _curator, _instanceId, stsc);

        getststoken.roleName(_ecsRole).call().subscribe(resp -> {
            LOG.info("ak_id {}/ak_secret {}/token {}\nExpiration:{}\nLastUpdated:{}",
                    resp.getAccessKeyId(), resp.getAccessKeySecret(), resp.getSecurityToken(),
                    resp.getExpiration(), resp.getLastUpdated());
            if (null != stsc) {
                if (stsc.getAccessKeyId().equals(resp.getAccessKeyId())
                    && stsc.getAccessKeySecret().equals(resp.getAccessKeySecret())
                    && stsc.getSecurityToken().equals(resp.getSecurityToken())) {
                    LOG.info("stsc for {} not changed, ignore", stscId);
                    return;
                }
            }
            LOG.info("stsc for {} changed! start to update content", stscId);
            final String stscPath = _ecsPath + "/sts_credentials." + _instanceId;
            try {
                createOrUpdateFor(stscPath)
                    .forPath(stscPath, stsAsText(_instanceId,
                            resp.getAccessKeyId(),
                            resp.getAccessKeySecret(),
                            resp.getSecurityToken(),
                            resp.getExpiration(),
                            resp.getLastUpdated()).getBytes(Charsets.UTF_8));
            } catch (final Exception e) {
                LOG.warn("exception when create or update sts_credentials, detail: {}", ExceptionUtils.exception2detail(e));
            }

        });
    }

    private static final String STS_TEMPLATE =
            "sts.instance_id=#sts.instance_id#\r\n"
            +"sts.ak_id=#sts.ak_id#\r\n"
            +"sts.ak_secret=#sts.ak_secret#\r\n"
            +"sts.token=#sts.token#\r\n"
            +"sts.expiration=#sts.expiration#\r\n"
            +"sts.lastupdated=#sts.lastupdated#\r\n"
            ;

    private String stsAsText(
            final String instance_id,
            final String ak_id,
            final String ak_secret,
            final String token,
            final String expiration,
            final String lastupdated) {
        String ret = replace(STS_TEMPLATE, "#sts.instance_id#", instance_id);
        ret = replace(ret, "#sts.ak_id#", ak_id);
        ret = replace(ret, "#sts.ak_secret#", ak_secret);
        ret = replace(ret, "#sts.token#", token);
        ret = replace(ret, "#sts.expiration#", expiration);
        ret = replace(ret, "#sts.lastupdated#", lastupdated);
        return ret;
    }

    private String replace(final String template, final String key, final String value) {
        return template.replace(key, null != value ? value : "");
    }

    private PathAndBytesable<?> createOrUpdateFor(final String path) throws Exception {
        if (null != this._curator.checkExists().forPath(path)) {
            LOG.info("update node: {}", path);
            return this._curator.setData();
        } else {
            LOG.info("create node: {}", path);
            return this._curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT);
        }
    }

    @Value("${ecs.path}")
    private String _ecsPath;

    @Value("${ecs.role}")
    String _ecsRole;

    @Value("${ecs.id}")
    String _instanceId;

    @Inject
    @Named("${zkconn.name}")
    CuratorFramework _curator;

    @Inject
    BeanHolder beanHolder;
}
