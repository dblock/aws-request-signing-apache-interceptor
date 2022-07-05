package io.github.acm19.aws.interceptor.test;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import software.amazon.awssdk.regions.Region;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * An AWS Request Signing Interceptor sample for arbitrary HTTP requests to an
 * Amazon OpenSearch Service domain.
 * </p>
 * <p>
 * The interceptor can also be used with the OpenSearch REST clients for
 * additional convenience and serialization.
 * </p>
 * <p>
 * Example usage with the OpenSearch low-level REST client:
 * </p>
 * 
 * <pre>
 * String serviceName = "es";
 * Aws4Signer signer = Aws4Signer.create();
 *
 * HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider,
 *         "us-east-1");
 *
 * return RestClient
 *         .builder(HttpHost.create("https://search-my-es-endpoint-gjhfgfhgfhg.us-east-1.amazonaws.com"))
 *         .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor))
 *         .build();
 * </pre>
 * <p>
 * Example usage with the OpenSearch high-level REST client:
 * </p>
 * 
 * <pre>
 * String serviceName = "es";
 * Aws4Signer signer = Aws4Signer.create();
 *
 * HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider,
 *         "us-east-1");
 *
 * return new RestHighLevelClient(RestClient
 *         .builder(HttpHost.create("https://search-my-es-endpoint-gjhfgfhgfhg.us-east-1.amazonaws.com"))
 *         .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
 * </pre>
 */
public class AmazonOpenSearchServiceSample extends Sample {
    private static final String ENDPOINT = "https://search-dblock-test-opensearch-21-tu5gqrjd4vg4qazjsu6bps5zsy.us-west-2.es.amazonaws.com";
    private static final Region REGION = Region.US_WEST_2;

    public static void main(String[] args) throws IOException {
        AmazonOpenSearchServiceSample sample = new AmazonOpenSearchServiceSample();
        sample.makeRequest();
        sample.indexDocument();
        sample.indexDocumentWithCompressionEnabled();
    }

    private void makeRequest() throws IOException {
        HttpGet httpGet = new HttpGet(ENDPOINT);
        logRequest("es", REGION, httpGet);
    }

    private void indexDocument() throws IOException {
        String payload = "{\"test\": \"val\"}";
        HttpPost httpPost = new HttpPost(ENDPOINT + "/index_name/type_name/document_id");
        httpPost.setEntity(stringEntity(payload));
        httpPost.addHeader("Content-Type", "application/json");
        logRequest("es", REGION, httpPost);
    }

    private void indexDocumentWithCompressionEnabled() throws IOException {
        String payload = "{\"test\": \"val\"}";
        HttpPost httpPost = new HttpPost(ENDPOINT + "/index_name/type_name/document_id");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(payload.getBytes("UTF-8"));
        gzipOutputStream.close();
        ByteArrayEntity entity = new ByteArrayEntity(
                outputStream.toByteArray(),
                ContentType.DEFAULT_BINARY);
        entity.setContentEncoding("gzip");
        httpPost.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
        httpPost.setEntity(entity);
        httpPost.addHeader("Content-Type", "application/json");
        logRequest("es", REGION, httpPost);
    }
}
