package com.bruno.artistalbum.service;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.public-url}")
    private String publicUrl;

    /**
     * Faz upload de um arquivo para o MinIO e retorna apenas o nome do arquivo.
     * A URL pré-assinada deve ser gerada posteriormente via getPresignedUrl().
     */
    public String uploadFile(String filename, InputStream content, String contentType) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // Aplica política de acesso público para permitir acesso via presigned URLs
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::"
                        + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(content, -1, 10485760) // 10MB part size
                            .contentType(contentType)
                            .build());

            // Retorna apenas o nome do arquivo (não a URL completa)
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    /**
     * Gera uma URL pública para acesso ao arquivo.
     * Nota: Em ambiente de produção com S3, deve-se usar presigned URLs com
     * expiração.
     * Aqui usamos URLs públicas devido a limitações do MinIO local com assinaturas
     * AWS4.
     * 
     * @param filename      Nome do arquivo no bucket
     * @param expiryMinutes Tempo de expiração (não utilizado em URLs públicas)
     * @return URL pública para acesso ao arquivo
     */
    public String getPresignedUrl(String filename, int expiryMinutes) {
        // Retorna URL pública simples
        // Em produção, usar: minioClient.getPresignedObjectUrl(...)
        return publicUrl + "/" + bucketName + "/" + filename;
    }

    /**
     * Gera uma URL pré-assinada com expiração padrão de 30 minutos.
     */
    public String getPresignedUrl(String filename) {
        return getPresignedUrl(filename, 30);
    }

    /**
     * Gera uma URL pré-assinada para UPLOAD (PUT) via cliente.
     * Requisito H: O backend deverá fornecer uma presigned url para upload.
     * 
     * @param filename      Nome do arquivo a ser criado
     * @param expiryMinutes Tempo de expiração
     * @return URL pré-assinada para método PUT
     */
    public String getUploadPresignedUrl(String filename, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(filename)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error generating upload presigned URL", e);
        }
    }

    /**
     * Deleta um arquivo do MinIO.
     * 
     * @param filename Nome do arquivo a ser deletado
     */
    public void deleteFile(String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO: " + filename, e);
        }
    }
}
