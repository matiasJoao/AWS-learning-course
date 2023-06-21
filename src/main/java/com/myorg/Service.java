package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.constructs.Construct;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;

import java.util.HashMap;
import java.util.Map;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class Service extends Stack {
    public Service(final Construct scope, final String id, Cluster cluster) {
        this(scope, id, null, cluster);
    }

    public Service(final Construct scope, final String id, final StackProps props, Cluster cluster) {
        super(scope, id, props);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://" + Fn.importValue("rds-endpoint")
                + ":3306/aws_project01?createDatabaseIfNotExist=true");
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));

        ApplicationLoadBalancedFargateService app =
                ApplicationLoadBalancedFargateService.Builder.create(this, id)
                .serviceName("Service_01")
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(2)
                .cluster(cluster)
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("AWS_V3-0-0")
                                .image(ContainerImage.fromRegistry("matias42/aws_project_course:3.0.0"))
                                .containerPort(8080)
                                .logDriver(LogDriver.awsLogs(
                                        AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, "Service01Log")
                                                        .logGroupName("Service_01")
                                                        .removalPolicy(RemovalPolicy.DESTROY)
                                                        .build())
                                                .streamPrefix("Service_01")
                                                .build()
                                ))
                                .environment(envVariables)
                                .build()
                ).publicLoadBalancer(true).build();

        app.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("8080")
                .healthyHttpCodes("200")
                .build()
        );

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
