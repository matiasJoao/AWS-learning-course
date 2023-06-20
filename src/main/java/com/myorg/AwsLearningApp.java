package com.myorg;

import software.amazon.awscdk.App;



public class AwsLearningApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "VpcTeste1");

        ClusterStack clusterStack = new ClusterStack(app, "ClusterTest1", vpcStack.getVpcStack());
        clusterStack.addDependency(vpcStack);

        Service service = new Service(app, "Service01", clusterStack.getCluster());
        service.addDependency(clusterStack);
        app.synth();
    }
}

