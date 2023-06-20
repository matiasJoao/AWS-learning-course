package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class VpcStack extends Stack {
    private Vpc vpcStack;
    public VpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
       vpcStack = Vpc.Builder.create(this, "Vpc")
                .maxAzs(2)
                .natGateways(0)
                .build();

    }

    public Vpc getVpcStack() {
        return vpcStack;
    }
}