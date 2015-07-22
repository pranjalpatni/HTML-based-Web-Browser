
/* 
 * @author Pranjal Patni
 * @email-id pxp142030@utdallas.edu
 * @version 1.0
 * 
 * This project mainly focuses on how to write a simple web browser. We will utilize Sockets for communication between processes. 
 */

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/*
 * WebBrowser class is getting a url from Command line and then fetching the HTML code of the page and displaying it in the console.
 * @ Variable : clientSocket is used socket created to communicate with the server.
 * @ Variable : in is the object of BufferedReader to read a from socket.
 * @ Variable : out is the object of PrintWriter to write to a socket.
 * @ Variable : q is a queue used to store parsed HTML text and images.
 * @ Variable : fos is a object of FileOutputStream which is used to write an image in file.
 */
public class WebBrowser {

    Socket clientSocket;
    BufferedReader in = null;
    PrintWriter out = null;
    Queue<String> q = new LinkedList<>();
    FileOutputStream fos;

    /*
     * bindSocket function binds a connection to the server using socket
     * @param : host is the url or IP address of the server.
     * @param : port is the port number at which we want to make a connection.
     * @return void. 
     */
    private void bindSocket(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Binding client socket failed");
            System.exit(-1);
        }
    }

    /*
     * connect method is reading and writing on the socket.
     * @param : host is the url or IP address of the server.
     * @param : port is the port number at which we want to make a connection.
     * @param : dest is the String in which content of the HTML page is stored.
     * @return void.
     */
    private void connect(String host, int port, String dest) {
        StringBuffer url = new StringBuffer();
        if (port != 80) {													// if port is not 80 then a new URL is created with other port number
            String host1 = host + ":" + port;
            url = makeURL(url, host1, dest);
        } else {															// if port is 80
            url = makeURL(url, host, dest);
        }
        String name = url.toString();
        out.println(name);													// GET request sent to the Server by writing on socket
        String line;
        StringBuilder output = new StringBuilder();
        try {
            while ((line = in.readLine()) != null) {
                output.append(line);
                if (output.toString().toLowerCase().contains("</html>")) {			// checks for the HTML tag in the returned content from the server.
                    break;
                }
            }
            String final_output = output.toString();
            String result = final_output.substring(final_output.indexOf("<html>") + 6, final_output.indexOf("</html>"));	// extract the content between HTML tags
            clientSocket.close();													// socket connection is closed.
            Scanner scan = new Scanner(result);
            scan.useDelimiter(">");											// it looks for the > in the extracted content
            while (scan.hasNext()) {
                String s = scan.next();
                if (s.contains(".jpg") || s.contains(".gif") || s.contains(".png") || s.contains(".jpeg")) {				// checks for Images
                    addImage(s);
                } else if (parseHTML(s)) {
                    if (s.charAt(0) != '<') {								// it looks for the < in the extracted content
                        Scanner scan2 = new Scanner(s);
                        scan2.useDelimiter("<");
                        q.add(scan2.next());								// content which is to be displayed is enqueued in q.
                    }
                }
            }
            while (q.size() != 0) {
                String out1 = q.remove();									// dequeue from the q
                if (out1.equals(" ")) {
                } else if (out1.contains(".jpg") || out1.contains(".gif") || out1.contains(".png") || out1.contains(".jpeg")) {			// checks if it is an image content
                    String[] dest1 = dest.split("/");
                    String[] check = out1.split("/");
                    System.out.println("Image: " + check[check.length-1]);
                    if (dest1.length > 1 && check.length > 1 && dest1[1].equals(check[1])) {
                        if (out1.charAt(0) != '/') {
                            getImage("/" + out1, host, port);
                        } else {
                            getImage(out1, host, port);
                        }
                    } else if (dest1.length > 2 && (!out1.contains("http://"))) {
                        String newOut1 = new String();
                        for (int i = 0; i < dest1.length - 1; i++) {
                            newOut1 = newOut1 + dest1[i] + "/";
                        }
                        getImage(newOut1 + out1, host, port);
                    } else {
                        if (!out1.contains("http://") && out1.charAt(0) != '/') {
                            getImage("/" + out1, host, port);
                        } else {
                            if (!out1.contains("http://")) {
                                getImage(out1, host, port);
                            } else {
                                String[] out2 = out1.split("//");
                                String out3 = out2[1].split("/")[0];
                                if (!out3.equals(host)) {
                                    getImage(out1, out3, port);
                                } else {
                                    getImage(out1, host, port);
                                }
                            }
                        }
                    }
                } else {
                    System.out.println(out1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in receiving");
            System.exit(-1);
        }
    }

    /*
     * parseHTML is a method which is used to parse HTML content of the page, it will check for HTML tags.
     * @param : s is a string which needs to be parsed.
     * @return : boolean; true if no HTML tag else false.
     */
    private boolean parseHTML(String s) {
        return !(s.charAt(0) == '\t' || (s.charAt(0) == '/' && s.charAt(1) == '/') || (s.charAt(0) == 'b' && s.charAt(1) == 'o' && s.charAt(2) == 'd' && s.charAt(3) == 'y'));		// checks for tab, comments and body tag
    }

    /*
     * getImage method gets the Image on the HTML page from the server and stores it to a local file on disk drive
     * @param : out1 is a string which has the path of the image on the server.
     * @param : host is the url or IP address of the server.
     * @param : port is the port number at which we want to make a connection.
     * @return : void
     */
    private void getImage(String out1, String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        InputStream IS = clientSocket.getInputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        StringBuffer imageURL = new StringBuffer();
        if (port != 80) {
            String host1 = host + ":" + port;
            imageURL = makeURL(imageURL, host1, out1);
        } else {
            imageURL = makeURL(imageURL, host, out1);
        }
        pw.println(imageURL.toString());											// GET request for the image to the server using socket
        ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();				// byteoutputstream to use to read bytes
        byte[] buffer = new byte[500];												// byte array to store image
        int flag;
        flag = IS.read(buffer);														// flag will store the size of the content from socket
        int sign = 0;
        while (buffer[sign] != 13 || buffer[sign + 1] != 10 || buffer[sign + 2] != 13) {		// finds the index where the header of the image ends
            sign++;
        }
        sign = sign + 4;
        buffer1.write(buffer, sign, flag - sign);									// writes the image to byte array after the header	
        flag = IS.read(buffer);
        while (flag != -1) {
            buffer1.write(buffer, 0, flag);											// write the image to byte array
            flag = IS.read(buffer);
        }
        File file = new File(getImageName(out1));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer1.toByteArray(), 0, buffer1.toByteArray().length);			// Writes the byte array into file.
        clientSocket.close();
    }


    /*
     * getImageName method will get the name of the image file from the path of the file.
     * @param : path is the path of the file on the server.
     * @return : String which is the name of the image file
     */
    private String getImageName(String path) {
        String[] image = path.split("/");
        return image[image.length - 1];
    }

    /*
     * addImage method will add the image name into the q.
     * @param :s is the image tag of HTML code.
     * @return void. 
     */
    private void addImage(String s) {
        String[] scan2 = s.split("src=");
        for (String scan21 : scan2) {
            if (scan21.contains(".jpg") || scan21.contains(".gif") || scan21.contains(".png") || scan21.contains(".jpeg")) {
                String[] scan3 = scan21.split(String.valueOf(scan21.charAt(0)));
                for (String scan31 : scan3) {
                    if (scan31.contains(".jpg") || scan31.contains(".gif") || scan31.contains(".png") || scan31.contains(".jpeg")) {
                        q.add(scan31);
                    }
                }
            }
        }
    }

    /*
     * MakeURL method append the Get request, Host and the path under host which is to be requested
     * @param : url  is the path which will be returned.
     * @param : host is the url or IP address of the server.
     * @param : dest is the destination of the page to be requested.
     * @retrun : StringBuffer which is the YRL of the make.
     */
    private StringBuffer makeURL(StringBuffer url, String host, String dest) {
        String newDest = dest;
        if (newDest.contains(host)) {
            newDest = dest.split(host)[1];
        }
        url.append("GET ");
        url.append(newDest);
        url.append(" HTTP/1.0 \r\nHost: ").append(host).append(" \r\n\r\n");
        return url;
    }

    /* 
     * Main Function which takes the URL as a command line argument and creates a WebBrowser object to fetch a HTML page.
     * @param : args is a string array which takes command line arguments
     * @ return void.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {												// if no command line argument is found
            System.out.println("Please enter the correct url");
            System.exit(-1);
        }
        String url = args[0];
        String[] address = url.split("/");
        String dest = new String();
        if (address.length == 1) {
            dest = "/";
        }
        for (int i = 1; i < address.length; i++) {
            dest = dest + "/" + address[i];
        }
        WebBrowser clientSocket = new WebBrowser();								// WebBrowser object to fetch HTML page 
        String host = address[0];
        int port = 80;
        if (host.contains(":")) {												// if port number is present in URL
            String[] newAddress = host.split(":");
            host = newAddress[0];
            port = Integer.parseInt(newAddress[1]);
        }

        if (dest.contains(".jpg") || dest.contains(".gif") || dest.contains(".png") || dest.contains(".jpeg")) {
            clientSocket.getImage(dest, host, port);
            System.exit(0);
        }
        clientSocket.bindSocket(host, port);
        clientSocket.connect(host, port, dest);
    }
}
