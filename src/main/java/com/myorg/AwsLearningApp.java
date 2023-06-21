package com.myorg;

import software.amazon.awscdk.App;



public class AwsLearningApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "VpcTeste1");

        ClusterStack clusterStack = new ClusterStack(app, "ClusterTest1", vpcStack.getVpcStack());
        clusterStack.addDependency(vpcStack);

        RdsStack rdsStack = new RdsStack(app, "Rds01", vpcStack.getVpcStack());
        rdsStack.addDependency(vpcStack);

        Service service = new Service(app, "Service1", clusterStack.getCluster());
        service.addDependency(clusterStack);
        service.addDependency(rdsStack);


        app.synth();
    }
}

