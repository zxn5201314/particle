package c27.com.particle.http;

/**
 * Created by huangjinqing on 2017/6/27.
 */
public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);
}
