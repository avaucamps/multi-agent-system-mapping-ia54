using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;

public class AgentManager : MonoBehaviour {

    public Camera agent;
    public Vector3 positionModel;
    public Vector3 scaleModel;
    public int numberOfAgents = 5;
    public bool isScreenshotEnabled = false;

    private Vector3 positionStart;
    private Vector3 positionEnd;
    private Dictionary<Camera, Vector3> agentsDict = new Dictionary<Camera, Vector3>();
    private string directoryPath = "";
    private NetworkManager networkManager;

    void Start () {
        networkManager = NetworkManager.Instance;

        directoryPath = "Session_" + System.DateTime.Now.ToString("dd-MM-yyyy_HH-mmss");
        Directory.CreateDirectory(directoryPath);
        SetBounds();
        SpawnCameras(numberOfAgents);

        if (isScreenshotEnabled)
        {
            StartCoroutine(TakeAllScreenshot());
        }
    }

    private void SpawnCameras(int number)
    {
        for (int i = 0; i < number; i++)
        {
            Vector3 position = GetRandomCoordinate();
            Camera agentInstance = Instantiate(
                agent,
                position,
                Quaternion.identity
            );
            agentInstance.enabled = false;
            agentInstance.transform.rotation = Quaternion.Euler(-90, 0, 0);
            agentsDict.Add(agentInstance, position);
        }
    }

    private void SetBounds()
    {
        positionStart = positionModel - (0.5f * scaleModel);
        positionEnd = positionModel + (0.5f * scaleModel);
    }

    private Vector3 GetRandomCoordinate()
    {
        return new Vector3(
            Random.Range(positionStart.x, positionEnd.x),
            Random.Range(positionStart.y, positionEnd.y),
            Random.Range(positionStart.z, positionEnd.z)
        );
    }

    IEnumerator TakeAllScreenshot()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict) {
            DisableAllCameras();
            pair.Key.enabled = true;
            yield return new WaitForEndOfFrame();
            networkManager.SendSpawnMessage(1, pair.Key.GetInstanceID().ToString());
            networkManager.SendMessage(pair.Key.GetInstanceID().ToString(), 2, pair.Value.ToString());
            TakeScreenshot(pair.Key);
        }

        networkManager.EndCommunication();
    }

    private void TakeScreenshot(Camera agent)
    {
        string filename = directoryPath + "\\" + agent.GetInstanceID().ToString() + ".png";
        ScreenCapture.CaptureScreenshot(filename);
        networkManager.SendMessage(agent.GetInstanceID().ToString(), 3, filename);
    }

    private void DisableAllCameras()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict)
        {
            pair.Key.enabled = false;
        }
    }
}