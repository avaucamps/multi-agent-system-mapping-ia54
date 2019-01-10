using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using UnityEngine.Networking;
using Random = UnityEngine.Random;

public class AgentManager : MonoBehaviour
{
    public Camera agent;
    public Vector3 positionModel;
    public Vector3 scaleModel;
    public int numberOfAgents = 150;
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
        sendWorldFeaturePoint = 4,
        sendNumberOfAgents = 5,
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
            Ray ray = pair.Key.ScreenPointToRay(screenPoint);
            RaycastHit hit = new RaycastHit();
            if(Physics.Raycast(ray, out hit))
            {
                return hit.point;
            }
            pair.Key.enabled = false;
        }

        return Vector3.zero;
    }

    private void SendAllWorldFeaturePoints()
    {
        shouldSendWorldFeaturePoints = false;
        foreach (FeaturePoint fp in featurePoints)
        {
            Vector3 worldPoint = Get3DPoint(fp.AgentId, fp.ScreenPoint);
            string newMessage = fp.FeatureMatchingType + "#";
            newMessage += fp.ScreenPoint.x + "#" + fp.ScreenPoint.y + "#";
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
            agentInstance.name = agentInstance.GetInstanceID().ToString();
            agentsDict.Add(agentInstance, position);
        }
        
        networkManager.SendMessage((int) MessageType.sendNumberOfAgents, number.ToString());
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
            0,
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