using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Runtime.CompilerServices;
using System.Runtime.Remoting.Messaging;
using System.Text;
using System.Threading;

public class NetworkManager : MonoBehaviour
{

    private TcpClient socketConnection;
    private Thread clientThread;
    private NetworkStream stream;
    private BinaryWriter writer;
    private StreamReader reader;
    private const String host = "localhost";
    private const Int32 port = 9991;
    private const string doneString = "Done";

    public delegate void ReceiveMessageAction(FeaturePoint featurePoint);
    public static event ReceiveMessageAction OnFeaturePointsReceived;
    
    # region Singleton

    public static NetworkManager Instance;

    private void Awake()
    {
        Instance = this;
    }

    # endregion

    void Start()
    {
        StartConnection();
    }

    public void SendSpawnMessage(int messageType, string message)
    {
        WriteSocket(messageType, message);
    }

    public void SendMessage(string agentId, int messageType, string message)
    {
        string newMessage = "#" + agentId + "#" + message;
        WriteSocket(messageType, newMessage);
    }

    public void EndCommunication()
    {
        if (socketConnection == null)
            return;

        byte endByte = 0;
        writer.Write(endByte);
        writer.Flush();
    }
    
    private void StartConnection()
    {
        try
        {
            clientThread = new Thread(Listen);
            clientThread.IsBackground = true;
            clientThread.Start();
        }
        catch (Exception e)
        {
            Debug.Log(e);
        }
    }

    private void Listen()
    {
        bool done = false;
        socketConnection = new TcpClient(host, port);
        stream = socketConnection.GetStream();
        reader = new StreamReader(stream);
        writer = new BinaryWriter(stream);
        string allMessages = "";
        Byte[] messageBytes = new Byte[256];
        
        int length;                         
        while ((length = stream.Read(messageBytes, 0, messageBytes.Length)) != 0 && !done)
        {
            var incomingData = new byte[length];
            Array.Copy(messageBytes, 0, incomingData, 0, length);
            string clientMessage = Encoding.ASCII.GetString(incomingData);
                
            allMessages += clientMessage;
            if (clientMessage.Contains(doneString))
            {
                allMessages = allMessages.Replace(doneString, "");
                break;
            }
        }
        
        ReadMessages(allMessages);
    }

    private void ReadMessages(string allMessages)
    {
        string[] messages = allMessages.Split(new string[] { "##" }, StringSplitOptions.None);
        foreach (string message in messages)
        {
            string[] splitString = message.Split('#');
            string agentId = splitString[1];
            float x = float.Parse(splitString[2]);
            float y = float.Parse(splitString[3]);

            OnFeaturePointsReceived(
                new FeaturePoint(agentId, x, y)
            );
        }
    }

    private void WriteSocket(int messageType, string theLine)
    {
        if (socketConnection == null)
        {
            return;
        }
        
        String sth = "\x01";
        String stx = "\x02";
        String etx = "\x03";
        String eot = "\x04";
        String foo = sth + messageType.ToString() + stx + theLine + etx + eot;

        byte[] b = System.Text.Encoding.UTF8.GetBytes(foo);
        string unicode = System.Text.Encoding.UTF8.GetString(b);
        byte[] buffer = System.Text.Encoding.ASCII.GetBytes(unicode);
        writer.Write(buffer);

        writer.Flush();
    }
}