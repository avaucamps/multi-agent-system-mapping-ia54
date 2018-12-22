public struct FeaturePoint
{
	string agentId;
	float x;
	float y;

	public FeaturePoint(string agentId, float x, float y)
	{
		this.agentId = agentId;
		this.x = x;
		this.y = y;
	}

	public string AgentId
	{
		get { return agentId; }
	}

	public float X
	{
		get { return x; }
	}

	public float Y
	{
		get { return y; }
	}
}