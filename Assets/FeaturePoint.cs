using UnityEngine;

public struct FeaturePoint
{
	string agentId;
	private Vector2 worldPoint;
	private Vector2 screenPoint;

	public FeaturePoint(string agentId, Vector2 worldPoint, Vector2 screenPoint)
	{
		this.agentId = agentId;
		this.worldPoint = worldPoint;
		this.screenPoint = screenPoint;
	}

	public string AgentId
	{
		get { return agentId; }
	}

	public Vector2 WorldPoint
	{
		get { return worldPoint; }
	}

	public Vector2 ScreenPoint
	{
		get { return screenPoint; }
	}
}