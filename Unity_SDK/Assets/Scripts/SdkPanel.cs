using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Text;

public class SdkPanel : MonoBehaviour {

    private StringBuilder m_Message = new StringBuilder();

    // Use this for initialization
    void Start () {
        StartCoroutine(DelayRegist());
	}

    IEnumerator DelayRegist()
    {
        yield return new WaitForSeconds(0.5f);
        if (MobileBridge.Instance != null)
        {
            MobileBridge.Instance.MessageCallback = InsertMessage;
        }
    }
	
	// Update is called once per frame
	void Update () {
        
	}

    public void InsertMessage(string msg)
    {
        m_Message.Append(msg);
        Debug.LogWarning(msg);
        if (m_Message.Length > 2000)
        {
            m_Message.Remove(0, m_Message.Length - 2000);
        }
    }

    float posY = 0f;
    float btnWidth = 150f;
    float btnHeight = 60f;
    void OnGUI()
    {
        posY = 0f;
        btnWidth = 150f;
        btnHeight = 70f;
        if (GUI.Button(new Rect(0, posY, btnWidth, btnHeight), "初始化SDK"))
        {
            InsertMessage("Unity:初始化SDK!");
            MobileBridge.Instance.InitSDK();
        }
        posY = posY + btnHeight + 10;
        if (GUI.Button(new Rect(0, posY, btnWidth, btnHeight), "登录"))
        {
            InsertMessage("Unity:登录!");
            MobileBridge.Instance.Login(1);
        }
        posY = posY + btnHeight + 10;
        if (GUI.Button(new Rect(0, posY, btnWidth, btnHeight), "登出"))
        {
            InsertMessage("Unity:登出!");
            MobileBridge.Instance.Logout();
        }
        posY = posY + btnHeight + 10;
        if (GUI.Button(new Rect(0, posY, btnWidth, btnHeight), "充值"))
        {
            InsertMessage("Unity:充值!");
            MobileBridge.Instance.Pay("1");
        }
        posY = posY + btnHeight + 10;
        if (GUI.Button(new Rect(0, posY, btnWidth, btnHeight), "退出游戏"))
        {
            InsertMessage("Unity:退出游戏!");
            MobileBridge.Instance.Exit();
        }
        posY = posY + btnHeight + 10;
        GUI.color = Color.red;
        GUI.Label(new Rect(0, posY, Screen.width, 300), m_Message.ToString());
    }
}
