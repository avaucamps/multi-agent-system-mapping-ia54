using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using Random = UnityEngine.Random;

public class AgentManager : MonoBehaviour
{
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
    private List<FeaturePoint> featurePoints = new List<FeaturePoint>();
    private bool shouldSendWorldFeaturePoints = false;

    private enum MessageType : int
    {
        sendNewAgent = 1,
        sendAgentPosition = 2,
        sendAgentScreenshot = 3,
        sendWorldFeaturePoint = 4
    }

    void Start()
    {
        networkManager = NetworkManager.Instance;

        directoryPath = "Session_" + System.DateTime.Now.ToString("dd-MM-yyyy_HH-mmss");
        if (isScreenshotEnabled)
        {
            Directory.CreateDirectory(directoryPath);
        }

        SetBounds();
        SpawnCameras(numberOfAgents);

        if (isScreenshotEnabled)
        {
            StartCoroutine(TakeAllScreenshot());
        }
    }

    private void Update()
    {
        if (shouldSendWorldFeaturePoints)
        {
            SendAllWorldFeaturePoints();
        }
    }

    private void OnEnable()
    {
        NetworkManager.OnFeaturePointReceived += StoreMessagedReceived;
        NetworkManager.OnAllFeaturePointsReceived += ShouldSendWorldFeaturePoints;
    }

    private void OnDisable()
    {
        NetworkManager.OnFeaturePointReceived -= StoreMessagedReceived;
        NetworkManager.OnAllFeaturePointsReceived -= ShouldSendWorldFeaturePoints;
    }

    private void StoreMessagedReceived(FeaturePoint featurePoint)
    {
        featurePoints.Add(featurePoint);
    }

    private void ShouldSendWorldFeaturePoints()
    {
        shouldSendWorldFeaturePoints = true;
    }

    private Vector3 Get3DPoint(string agentId, Vector2 screenPoint)
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict)
        {
            if (pair.Key.name != agentId) continue;
            pair.Key.enabled = true;
            Ray ray = agent.ScreenPointToRay(new Vector3(screenPoint.x, screenPoint.y));
            Vector3 point = ray.origin + (ray.direction * agent.nearClipPlane);
            pair.Key.enabled = false;

            return point;
        }

        return Vector3.zero;
    }

    private void SendAllWorldFeaturePoints()
    {
        shouldSendWorldFeaturePoints = false;
        foreach (FeaturePoint fp in featurePoints)
        {
            Vector3 worldPoint = Get3DPoint(fp.AgentId, fp.ScreenPoint);
            string newMessage = fp.ScreenPoint.x + "#" + fp.ScreenPoint.y + "#";
            newMessage += worldPoint.x + "#" + worldPoint.y + "#";
            networkManager.SendMessage(
                (int) MessageType.sendWorldFeaturePoint,
                fp.AgentId, 
                newMessage
             );
        }
        
        networkManager.EndCommunication();
        featurePoints.Clear();
    }

    private void SpawnCameras(int number)
    {
        /*for (int i = 0; i < number; i++)
        {
            Vector3 position = GetRandomCoordinate();
            Camera agentInstance = Instantiate(
                agent,
                position,
                Quaternion.identity
            );
            //agentInstance.enabled = false;
            agentInstance.transform.rotation = Quaternion.Euler(-90, 0, 0);
            agentInstance.name = agentInstance.GetInstanceID().ToString();
            agentsDict.Add(agentInstance, position);
        }*/
        Vector3 position = new Vector3(1.455f, -0.332f, 4.611f);
        Camera agentInstance = Instantiate(
            agent,
            position,
            Quaternion.identity
        );
        agentInstance.fieldOfView = 70;
        agentInstance.enabled = false;
        agentInstance.transform.rotation = Quaternion.Euler(-90, 0, 0);
        agentInstance.name = agentInstance.GetInstanceID().ToString();
        agentsDict.Add(agentInstance, position);

        Vector3 position2 = new Vector3(1.037f, -0.332f, 3.745f);
        Camera agentInstance2 = Instantiate(
            agent,
            position2,
            Quaternion.identity
        );
        agentInstance2.fieldOfView = 70;
        agentInstance2.enabled = false;
        agentInstance2.transform.rotation = Quaternion.Euler(-90, 0, 0);
        agentInstance2.name = agentInstance2.GetInstanceID().ToString();
        agentsDict.Add(agentInstance2, position2);

        Vector3 position3 = new Vector3(2.115f, -0.332f, 5.336f);
        Camera agentInstance3 = Instantiate(
            agent,
            position3,
            Quaternion.identity
        );
        agentInstance3.fieldOfView = 70;
        agentInstance3.enabled = false;
        agentInstance3.transform.rotation = Quaternion.Euler(-90, 0, 0);
        agentInstance3.name = agentInstance3.GetInstanceID().ToString();
        agentsDict.Add(agentInstance3, position3);
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

    private IEnumerator TakeAllScreenshot()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict)
        {
            DisableAllCameras();
            pair.Key.enabled = true;
            yield return new WaitForEndOfFrame();
            networkManager.SendMessage(
                (int) MessageType.sendNewAgent, 
                pair.Key.GetInstanceID().ToString(),
                null
            );
            networkManager.SendMessage(
                (int) MessageType.sendAgentPosition,
                pair.Key.GetInstanceID().ToString(), 
                pair.Value.ToString()
            );
            TakeScreenshot(pair.Key);
        }

        DisableAllCameras();
        networkManager.EndCommunication();
    }

    private void TakeScreenshot(Camera agent)
    {
        string filename = directoryPath + "\\" + agent.GetInstanceID().ToString() + ".png";
        ScreenCapture.CaptureScreenshot(filename);
        networkManager.SendMessage(
            (int) MessageType.sendAgentScreenshot, 
            agent.GetInstanceID().ToString(), 
            filename
        );
    }

    private void DisableAllCameras()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict)
        {
            pair.Key.enabled = false;
        }
    }
}