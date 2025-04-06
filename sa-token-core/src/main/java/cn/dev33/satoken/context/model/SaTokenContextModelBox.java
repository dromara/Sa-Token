package cn.dev33.satoken.context.model;

/**
 * Box 盒子类，用于存储 [ SaRequest、SaResponse、SaStorage ] 三个包装对象
 *
 * @author click33
 * @since 1.16.0
 */
public class SaTokenContextModelBox {

    public SaRequest request;

    public SaResponse response;

    public SaStorage storage;

    public SaTokenContextModelBox(SaRequest request, SaResponse response, SaStorage storage) {
        this.request = request;
        this.response = response;
        this.storage = storage;
    }

    public SaRequest getRequest() {
        return request;
    }

    public void setRequest(SaRequest request) {
        this.request = request;
    }

    public SaResponse getResponse() {
        return response;
    }

    public void setResponse(SaResponse response) {
        this.response = response;
    }

    public SaStorage getStorage() {
        return storage;
    }

    public void setStorage(SaStorage storage) {
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "Box [request=" + request + ", response=" + response + ", storage=" + storage + "]";
    }

}