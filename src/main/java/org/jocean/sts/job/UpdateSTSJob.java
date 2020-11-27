package org.jocean.sts.job;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class UpdateSTSJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSTSJob.class);

    void update() {
        LOG.info("update: zkconn info {}, ecs instance {}", _curator, _instanceId);
    }

    @Inject
    @Named("${zkconn.name}")
    CuratorFramework _curator;

    @Value("${ecs.id}")
    String _instanceId;
}
