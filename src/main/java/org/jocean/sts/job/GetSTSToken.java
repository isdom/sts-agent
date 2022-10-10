package org.jocean.sts.job;

import javax.inject.Inject;

import org.jocean.aliyun.ecs.MetadataAPI;
import org.jocean.http.RpcExecutor;
import org.jocean.rpc.RpcDelegater;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
class GetSTSToken implements FactoryBean<MetadataAPI.STSTokenBuilder> {

    @Override
    public MetadataAPI.STSTokenBuilder getObject() throws Exception {
        return RpcDelegater.rpc(MetadataAPI.STSTokenBuilder.class).emitter(inter2any -> _executor.submit(inter2any)).build();
    }

    @Override
    public Class<?> getObjectType() {
        return MetadataAPI.STSTokenBuilder.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Inject
    RpcExecutor _executor;
}
