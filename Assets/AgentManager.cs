using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;

public class AgentManager : MonoBehaviour {

    public float xOrigin = 0;
    public float yOrigin = 0;
    public float zOrigin = 0;
    public float xScale = 0;
    public float yScale = 0;
    public float zScale = 0;
    public int numberOfAgents = 5;

    private float xStart;
    private float xEnd;
    private float yStart;
    private float yEnd;
    private float zStart;
    private float zEnd;
    private Dictionary<Camera, Vector3> agentsDict = new Dictionary<Camera, Vector3>();
    private string directoryPath = "";

	void Start () {
        directoryPath = "Session_" + System.DateTime.Now.ToString("yyyy-MM-dd HH-mm-ss");
        Directory.CreateDirectory(directoryPath);
        SetBounds();
        SpawnCameras(numberOfAgents);
        StartCoroutine(TakeAllScreenshot());
    }

    private void SpawnCameras(int number)
    {
        for (int i = 0; i < number; i++)
        {
            Vector3 position = GetRandomCoordinate();
            Camera agentInstance = Instantiate(
                Resources.Load("Agent", typeof(Camera)),
                position,
                Quaternion.identity
            ) as Camera;
            agentInstance.enabled = false;
            agentInstance.transform.rotation = Quaternion.Euler(-90, 0, 0);
            agentsDict.Add(agentInstance, position);
        }
    }

    private void SetBounds()
    {
        xStart = xOrigin - (0.5f * xScale);
        xEnd = xOrigin + (0.5f * xScale);
        yStart = yOrigin - (0.5f * yScale);
        yEnd = yOrigin + (0.5f * yScale);
        zStart = zOrigin - (0.5f * zScale);
        zEnd = zOrigin + (0.5f * zScale);
    }

    private Vector3 GetRandomCoordinate()
    {
        return new Vector3(
            Random.Range(xStart, xEnd),
            Random.Range(yStart, yEnd),
            Random.Range(zStart, zEnd)
        );
    }

    IEnumerator TakeAllScreenshot()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict) {
            DisableAllCameras();
            pair.Key.enabled = true;
            yield return new WaitForEndOfFrame();
            TakeScreenshot(pair.Key, pair.Value);
        }
    }

    private void TakeScreenshot(Camera agent, Vector3 position)
    {
        string filename = directoryPath + "\\" +  position.x + "_" + position.y + "_" + position.z + ".png";
        ScreenCapture.CaptureScreenshot(filename);
    }

    private void DisableAllCameras()
    {
        foreach (KeyValuePair<Camera, Vector3> pair in agentsDict)
        {
            pair.Key.enabled = false;
        }
    }
}