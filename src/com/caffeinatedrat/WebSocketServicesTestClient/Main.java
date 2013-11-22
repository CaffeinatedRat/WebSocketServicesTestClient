package com.caffeinatedrat.WebSocketServicesTestClient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.caffeinatedrat.SimpleWebSockets.*;
import com.caffeinatedrat.SimpleWebSockets.Exceptions.InvalidFrameException;
import com.caffeinatedrat.SimpleWebSockets.Frames.Frame;
import com.caffeinatedrat.SimpleWebSockets.Frames.Frame.OPCODE;
import com.caffeinatedrat.SimpleWebSockets.Frames.FrameReader;

public class Main {

    public static void main(String[] args) {
        
        Socket socket;
        try {
            
            //Your WebSocket server address and port here.
            socket = new Socket("192.168.1.100", 25564);
            
            //Create the handshake.
            Handshake handshake = new Handshake(socket);
            
            //Provide the origin of the domain that is sending the request.
            handshake.setOrigin("http://wwwlocal.minecraft.com");
            
            //Set the UserAgent, Firefox will set this to Mozilla/5.0 for example.
            handshake.setUserAgent("SimpleWebSocket TestClient/1.0");
            
            //Create the handshake request and send it.
            if (handshake.createRequest()) {
                
                //Block until the server has completed the response successfully.
                if(handshake.negotiateResponse()) {
                    
                    /*
                    
                    //Single Frame
                    Frame frame = new Frame(socket);
                    frame.setOpCode(OPCODE.TEXT_DATA_FRAME);
                    frame.setFinalFragment();
                    frame.setPayload("info");
                    frame.write();
                    
                    */
                    
                    //Fragmented Frame
                    Frame frame = new Frame(socket);
                    frame.setOpCode(OPCODE.TEXT_DATA_FRAME);
                    frame.setPayload("player");
                    frame.write();
                    
                    frame = new Frame(socket);
                    frame.setOpCode(OPCODE.CONTINUATION_DATA_FRAME);
                    frame.setPayload(" caffeinated");
                    frame.write();

                    frame = new Frame(socket);
                    frame.setOpCode(OPCODE.CONTINUATION_DATA_FRAME);
                    frame.setFinalFragment();
                    frame.setPayload("rat");
                    frame.write();
                    
                    
                }
            }
            
            //Create the FrameReader without an EventLayer with a large timeout value and low fragmentation acceptance.
            FrameReader frame = new FrameReader(socket, null, 30000, 2);
            
            //Read until the socket is closed.
            while(!socket.isClosed()) {
                
                //Read one or many frames.
                if(frame.read()) {
                    
                    //If a frame contains a close control frame, terminate the connection.
                    if (frame.getFrameType() == OPCODE.CONNECTION_CLOSE_CONTROL_FRAME) {
                        socket.close();
                        break;
                    }
                    //Output text only.
                    else {
                        System.out.println(frame.getTextPayload().toString());
                    }
                }
                
                //Wait...
                Thread.sleep(1000);
            }
            
        } catch (InvalidFrameException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}