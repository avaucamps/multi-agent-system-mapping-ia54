using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AgentSpawner : MonoBehaviour {

    public float xOrigin = 0;
    public float yOrigin = 0;
    public float zOrigin = 0;
    public float xScale = 0;
    public float yScale = 0;
    public float zScale = 0;

    private float xStart;
    private float xEnd;
    private float yStart;
    private float yEnd;
    private float zStart;
    private float zEnd;

	void Start () {
        SetBounds();
        SpawnCameras(100);
	}

    private void SpawnCameras(int number)
    {
        for (int i = 0; i < number; i++)
        {
            GameObject agentInstance = Instantiate(
                Resources.Load("Agent"),
                GetRandomCoordinate(),
                Quaternion.identity
            ) as GameObject;

            agentInstance.transform.rotation = Quaternion.Euler(-90, 0, 0);
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
}
