using UnityEngine;
using System.Collections;
using System;
using System.IO;
using System.Net.Sockets;
public class NetworkManager : MonoBehaviour
{
    private Boolean socketReady = false;
    private TcpClient mySocket;
    private NetworkStream theStream;
    private BinaryWriter theWriter;
    private StreamReader theReader;
    private const String host = "localhost";
    private const Int32 port = 9991;

    # region Singleton

    public static NetworkManager Instance;

    private void Awake()
    {
        Instance = this;
    }

    # endregion

    void Start()
    {
        SetupSocket();
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
        if (!socketReady)
            return;

        byte endByte = 0;
        theWriter.Write(endByte);
        theWriter.Flush();
    }

    private void SetupSocket()
    {
        try
        {
            mySocket = new TcpClient(host, port);
            theStream = mySocket.GetStream();
            theWriter = new BinaryWriter(theStream);
            theReader = new StreamReader(theStream);
            socketReady = true;
        }
        catch (Exception e)
        {
            Debug.Log("Socket error: " + e);
        }
    }

    private void WriteSocket(int messageType, string theLine)
    {
        if (!socketReady)
            return;
        String sth = "\x01";
        String stx = "\x02";
        String etx = "\x03";
        String eot = "\x04";
        String foo = sth + messageType.ToString() + stx + theLine + etx + eot;

        byte[] b = System.Text.Encoding.UTF8.GetBytes(foo);
        string unicode = System.Text.Encoding.UTF8.GetString(b);
        byte[] buffer = System.Text.Encoding.ASCII.GetBytes(unicode);
        theWriter.Write(buffer);

        theWriter.Flush();
    }

    private String ReadSocket()
    {
        if (!socketReady)
            return "";
        if (theStream.DataAvailable)
            return theReader.ReadLine();
        return "";
    }

    private void CloseSocket()
    {
        if (!socketReady)
            return;
        theWriter.Close();
        theReader.Close();
        mySocket.Close();
        socketReady = false;
    }
}