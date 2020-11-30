package org.jocean.sts.job;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.curator.framework.CuratorFramework;
import org.jocean.aliyun.ecs.MetadataAPI;
import org.jocean.svr.annotation.RpcFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class UpdateSTSJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSTSJob.class);

    void update() {
        LOG.info("update by {}: zkconn info {}, ecs instance {}", this, _curator, _instanceId);
        getststoken.roleName(_ecsRole).call().subscribe(resp ->
            LOG.info("ak_id {}/ak_secret {}/token {}",
                    resp.getAccessKeyId(), resp.getAccessKeySecret(), resp.getSecurityToken()));
    }

    @RpcFacade
    MetadataAPI.STSTokenBuilder  getststoken;

    @Value("${ecs.role}")
    String _ecsRole;

    @Inject
    @Named("${zkconn.name}")
    CuratorFramework _curator;

    @Value("${ecs.id}")
    String _instanceId;
}
