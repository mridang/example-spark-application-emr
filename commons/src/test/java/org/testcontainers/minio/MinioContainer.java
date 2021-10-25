package org.testcontainers.minio;

import java.net.InetSocketAddress;
import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import org.testcontainers.utility.Base58;
import org.testcontainers.utility.DockerImageName;

/**
 * Represents an MinIO docker instance which exposes by default port 9000.
 * The docker image is by default fetched from minio/minio
 */
public class MinioContainer extends GenericContainer<MinioContainer> {

    /**
     * MinIO Default version
     */
    @Deprecated
    protected static final String DEFAULT_TAG = "latest";
    /**
     * MinIO Default HTTP port
     */
    private static final int MINIO_DEFAULT_PORT = 9000;
    /**
     * MinIO Health-check endpoint
     */
    private static final String MINIO_HEALTH_ENDPOINT = "/minio/health/ready";
    /**
     * MinIO storage directory
     */
    private static final String MINIO_DEFAULT_STORAGE_DIRECTORY = "/data";
    /**
     * MinIO Docker base image
     */
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("minio/minio");

    public MinioContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    /**
     * Create a Minio Container by passing the full docker image name
     *
     * @param dockerImageName Full docker image name as a {@link String}, like: minio/minio:7.9.2
     */
    public MinioContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    /**
     * Create an Minio Container by passing the full docker image name
     *
     * @param dockerImageName Full docker image name as a {@link DockerImageName}, like: DockerImageName.parse("minio/minio:7.9.2")
     */
    public MinioContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);

        logger().info("Starting an minio container using [{}]", dockerImageName);
        withNetworkAliases("minio-" + Base58.randomString(6));
        withEnv("MINIO_ACCESS_KEY", "minio_username");
        withEnv("MINIO_SECRET_KEY", "minio_password");
        addExposedPorts(MINIO_DEFAULT_PORT);
        withCommand("server", "/" + RandomStringUtils.randomAlphanumeric(8));
        setWaitStrategy(new HttpWaitStrategy()
                .forPort(MINIO_DEFAULT_PORT)
                .forPath(MINIO_HEALTH_ENDPOINT)
                .withStartupTimeout(Duration.ofMinutes(2)));
    }

    /**
     * Define the MinIO username to be used
     *
     * @param username Username to set
     * @return this
     */
    @SuppressWarnings("unused")
    public MinioContainer withUsername(String username) {
        withEnv("MINIO_ACCESS_KEY", username);
        return this;
    }

    /**
     * Define the MinIO password to be used
     *
     * @param password Password to set
     * @return this
     */
    @SuppressWarnings("unused")
    public MinioContainer withPassword(String password) {
        withEnv("MINIO_SECRET_KEY", password);
        return this;
    }

    public String getHttpHostAddress() {
        return getHost() + ":" + getMappedPort(MINIO_DEFAULT_PORT);
    }

    public InetSocketAddress getTcpHost() {
        return new InetSocketAddress(getHost(), getMappedPort(MINIO_DEFAULT_PORT));
    }
}
