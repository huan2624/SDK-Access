using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using System;

public class MobileBridge : MonoBehaviour {
    
    [HideInInspector]
    public static MobileBridge Instance;

    public Action<string> MessageCallback;

    [DllImport("__Internal")]
    private static extern void iosInitSDK();
    [DllImport("__Internal")]
    private static extern void iosLogin(int platform);
    [DllImport("__Internal")]
    private static extern void iosLogout();
    [DllImport("__Internal")]
    private static extern void iosPay(string json);
    [DllImport("__Internal")]
    private static extern void iosExit();
    [DllImport("__Internal")]
    private static extern void iosInitUpAvatarConfig(int quality, int size, string playerId, string url);
    [DllImport("__Internal")]
    private static extern void iosOpenUpAvatarDialog(string num, string site);

    // Use this for initialization
    void Start () {
        Instance = this;
        DontDestroyOnLoad(this.gameObject);
    }

    #region 登录及支付
    /// <summary>
    /// 初始化
    /// </summary>
    /// <param name="platform"></param>
    public void InitSDK()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("initSDK");
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
            iosInitSDK();
#endif
    }

    /// <summary>
    /// 登录
    /// </summary>
    /// <param name="platform"></param>
    public void Login(int platform)
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("login", platform);
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
            iosLogin(platform);
#endif
    }

    /// <summary>
    /// 登出
    /// </summary>
    public void Logout()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("logout");
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
            iosLogout();
#endif
    }

    /// <summary>
    /// 退出游戏
    /// </summary>
    public void Exit()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("exit");
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
            iosExit();
#endif
    }

    /// <summary>
    /// 支付
    /// </summary>
    /// <param name="money"></param>
    public void Pay(string json)
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("pay", json);
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
            iosPay(json);
#endif
    }
    #endregion

    #region 头像上传
    /// <summary>
    /// 初始化头像上传信息
    /// </summary>
    /// <param name="playerId"></param>
    /// <param name="url"></param>
    public void InitAvatar(string playerId, string url)
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            string imgServer = url + "upImg.do";
            jc.CallStatic("initUpAvatarConfig", 100, 320, playerId, imgServer);
            //jc.CallStatic("initUpAvatarConfig", 100, 320, "0003", "http://119.29.10.120:8510/img/upImg.do");
        }
#endif

//#if UNITY_IOS && !UNITY_EDITOR
//        string imgServer = url + "upImg.do";
//        iosInitUpAvatarConfig(100, 320, playerId, imgServer);
//#endif

    }

    /// <summary>
    /// 上传头像
    /// </summary>
    public void UploadAvatar()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        using (AndroidJavaClass jc = new AndroidJavaClass("com.bear.bridge.MNative"))
        {
            jc.CallStatic("openUpAvatarDialog", "1", "1");
        }
#endif

#if UNITY_IOS && !UNITY_EDITOR
        iosOpenUpAvatarDialog("1", "1");
#endif
    }
    #endregion

    /// <summary>
    /// 上传头像返回
    /// </summary>
    /// <param name="msg"></param>
    void UpAvatarBack(string msg)
    {
        RecordMessage(msg);
    }

    /// <summary>
    /// 登录返回
    /// </summary>
    /// <param name="msg"></param>
    void NativeLoginBack(string msg)
    {
        RecordMessage(msg);
    }

    /// <summary>
    /// 支付返回
    /// </summary>
    /// <param name="msg"></param>
    void NativePayBack(string msg)
    {
        RecordMessage(msg);
    }

    /// <summary>
    /// sdk调用登出
    /// </summary>
    /// <param name="msg"></param>
    void NativeLogout(string msg)
    {
        RecordMessage(msg);
    }

    public void RecordMessage(string msg)
    {
        if (MessageCallback != null)
        {
            MessageCallback.Invoke("ReceiveMessage:" + msg);
        }
    }
}
