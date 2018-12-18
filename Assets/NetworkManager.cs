using UnityEngine;
using System.Collections;
using System;
using System.IO;
using System.Net.Sockets;
using System.Runtime.CompilerServices;
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
        Byte[] bytes = new Byte[256];
        
        while (!done)
        {
            int length;

            Debug.Log(stream.ReadByte());
            if (stream.ReadByte() == 1)
            {
                while (stream.ReadByte() != 4)
                {
                    Debug.Log("start");
                    Debug.Log(stream.ReadByte());
                }
                Debug.Log("end");
            }
            
            /*while (stream.ReadByte() != 4)
            {
                length = stream.Read(bytes, 0, bytes.Length);
                var incommingData = new byte[length]; 						
                Array.Copy(bytes, 0, incommingData, 0, length);						
                string serverMessage = Encoding.ASCII.GetString(incommingData); 						
                Debug.Log("server message received as: " + serverMessage); 					
            } */
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