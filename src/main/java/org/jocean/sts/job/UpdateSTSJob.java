package org.jocean.sts.job;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSTSJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateSTSJob.class);

    void update() {
        LOG.info("update: zkconn info {}", _curator);
    }

    @Inject
    @Named("${zkconn.name}")
    CuratorFramework _curator;
}
