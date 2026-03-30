package com.bizcore.catalog.infrastructure.storage;

import com.bizcore.catalog.domain.port.out.StoragePort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class R2StorageAdapter implements StoragePort {

    private final R2Properties properties;
    private S3Client s3;

    @PostConstruct
    void init() {
        if (properties.getEndpoint() == null || properties.getEndpoint().isBlank()) {
            return; // R2 no configurado (entorno local/test)
        }
        s3 = S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
                ))
                .region(Region.of("auto"))
                .build();
    }

    @Override
    public String upload(String key, String contentType, long sizeBytes, InputStream content) {
        if (s3 == null) {
            throw new IllegalStateException("Cloudflare R2 not configured (R2_ENDPOINT is empty)");
        }
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .contentLength(sizeBytes)
                .build();
        s3.putObject(request, RequestBody.fromInputStream(content, sizeBytes));
        return buildPublicUrl(key);
    }

    @Override
    public void delete(String key) {
        if (s3 == null) return; // sin R2, no hay nada que borrar
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build());
    }

    @Override
    public String buildPublicUrl(String key) {
        String base = properties.getPublicUrl();
        if (base == null || base.isBlank()) return key;
        return base.endsWith("/") ? base + key : base + "/" + key;
    }
}
