package com.pm.stack;

import software.amazon.awscdk.*;

public class LocalStack extends Stack {

    public LocalStack(final App scope, final String name, final StackProps props) {
        super(scope, name, props);
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();
        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}
