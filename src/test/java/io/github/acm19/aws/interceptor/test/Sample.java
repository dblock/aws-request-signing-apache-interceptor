package io.github.acm19.aws.interceptor.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import io.github.acm19.aws.interceptor.http.AwsRequestSigningApacheInterceptor;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.http.SdkRequestContext;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

class Sample {
    void logRequest(String serviceName, Region region, SdkHttpFullRequest request) throws IOException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        Aws4Signer signer = Aws4Signer.create();
        ExecutionAttributes attrs = new ExecutionAttributes()
            .putAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS, DefaultCredentialsProvider.create().resolveCredentials())
            .putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, serviceName)
            .putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, region);
        SdkHttpFullRequest signedRequest = signer.sign(request, attrs);

        HttpExecuteResponse executeResponse = httpClient.prepareRequest(
            HttpExecuteRequest.builder().request(signedRequest).build()
        ).call();
        
        SdkHttpResponse response = executeResponse.httpResponse();
        System.out.println(response.statusCode() + " " + response.statusText());
        if (! response.isSuccessful()) {
            throw new RuntimeException(response.statusCode() + " " + response.statusText());            
        }
        
        System.out.println(IoUtils.toUtf8String(executeResponse.responseBody().orElse(null)));
    }
}
