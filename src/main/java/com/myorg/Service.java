package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class Service extends Stack {
    public Service(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);
        ApplicationLoadBalancedFargateService app =
                ApplicationLoadBalancedFargateService.Builder.create(this, id)
                .serviceName("Serive01")
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .cluster(cluster)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("awsproject")
                                .image(ContainerImage.fromRegistry("matias42/aws_project_course:tagname"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(
                                        AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, "Service01Log")
                                                        .logGroupName("Service01")
                                                        .removalPolicy(RemovalPolicy.DESTROY)
                                                        .build())
                                                .streamPrefix("Service01")
                                                .build()
                                ))
                                .build()
                ).publicLoadBalancer(true).build();

        ScalableTaskCount scalableTaskCount = app.getService().autoScaleTaskCount(EnableScalingProps.builder()
                        .minCapacity(02)
                        .maxCapacity(04)
                .build());
        scalableTaskCount.scaleOnCpuUtilization("Service01AutoScalling", CpuUtilizationScalingProps.builder()
                        .targetUtilizationPercent(50)
                        .scaleInCooldown(Duration.seconds(60))
                        .scaleOutCooldown(Duration.seconds(60))
                .build());


    }
}
