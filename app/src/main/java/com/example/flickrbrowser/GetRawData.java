package com.example.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// 下载状态的枚举，包含多种状态
enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

// 异步任务类，用于获取原始数据
class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";
    private DownloadStatus mDownloadStatus; // 下载状态
    private final OnDownloadComplete mCallback; // 回调接口，用于在下载完成后通知调用者

    // 回调接口，用于在下载完成后通知调用者
    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    // 构造函数，接受一个回调接口实例
    public GetRawData(OnDownloadComplete callback) {
        this.mDownloadStatus = DownloadStatus.IDLE; // 初始化下载状态为IDLE
        mCallback = callback; // 初始化回调接口实例
    }

    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread starts");
//        onPostExecute(doInBackground(s));
        if (mCallback != null) {
//         String result = doInBackground(s);
//         mCallback.onDownloadComplete(result,mDownloadStatus);
            mCallback.onDownloadComplete(doInBackground(s), mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread ends");
    }

    // 在任务完成后调用，用于更新UI或通知调用者
    @Override
    protected void onPostExecute(String s) {
//        Log.d(TAG, "onPostExecute: parameter =" + s); // 打印下载的数据
        if (mCallback != null) {
            mCallback.onDownloadComplete(s, mDownloadStatus); // 调用回调接口方法，传递下载数据和状态
        }
        Log.d(TAG, "onPostExecute: ends"); // 任务完成日志
    }

    // 在后台线程中执行下载任务
    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null; // HTTP连接
        BufferedReader reader = null; // 缓冲读取器
        if (strings == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALISED; // 如果参数为空，设置状态为未初始化
            return null;
        }
        try {
            mDownloadStatus = DownloadStatus.PROCESSING; // 设置状态为处理中
            URL url = new URL(strings[0]); // 获取URL
            connection = (HttpURLConnection) url.openConnection(); // 打开连接
            connection.setRequestMethod("GET"); // 设置请求方法为GET
            connection.connect(); // 连接到服务器
            int response = connection.getResponseCode(); // 获取响应码
            Log.d(TAG, "doInBackground: The response code was " + response); // 打印响应码
            StringBuilder result = new StringBuilder(); // 用于保存结果的StringBuilder
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 初始化读取器
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.append(line).append("\n"); // 逐行读取数据并追加到结果中
            }
            mDownloadStatus = DownloadStatus.OK; // 设置状态为成功
            return result.toString(); // 返回结果
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage()); // URL格式错误
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage()); // IO异常
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?" + e.getMessage()); // 安全异常，需要权限
        } finally {
            if (connection != null) {
                connection.disconnect(); // 断开连接
            }
            if (reader != null) {
                try {
                    reader.close(); // 关闭读取器
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream" + e.getMessage()); // 关闭流时出现错误
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY; // 设置状态为失败或空
        return null;
    }
}
