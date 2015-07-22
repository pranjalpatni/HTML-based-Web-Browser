DESCRIPTION:

The project is about implementing the working of a web browser, which sends a GET HTTP request to a website and fetches the actual page, thus removing the entire html tags associated to it. If the page contains images, then another HTTP GET request is sent for each image and they are saved in the PC. The output needs to be printed according to the order in which the data is received from the web server.

Socket programming has been used to establish a TCP connection with the web server, and the subsequent GET request fetches the html page. The language that has been used to make this project is Java. Since an actual web server is being used at the other end, hence this project focuses only on the client side. 

CHALLENGES FACED:

1- Required Socket Programming-

Without knowing how the server and the client communicate using sockets, this project would not have been possible. At first, a simple client-server program was created where the client sends a “hello” and the server replies with a “hi”.

2- After having established a proper TCP connection with the web server, the next challenge was to send the GET Request. The web server would only send the html page if the format of the GET request would be exactly similar to the standard convention.

3- Maintaining the proper order of the information to be printed-

A queue is used which would add the data as it gets parsed. Once the whole data has been added, it is removed according to FIFO order.

4- Parsing the data-

The use of any parser tool was prohibited, so a customized technique for parsing was needed to implemented. The data is parsed using “>” delimiter and the next character after it is checked. If that character is a “<” or a “\t”, then it is not added to the queue. Otherwise, it gets added. There are some other exceptions that had to be made in order to parse out some other unwanted data.


HOW TO COMPILE THE CODE:

javac WebBrowser.java


HOW TO RUN THE CODE:

The program takes a command line argument of the url of the page. Enter the url as given by the professor, but do not add "http://". For example-

1- java WebBrowser www.december.com/html/demo/hello.html

2- java WebBrowser assets.climatecentral.org/images/uploads/news/Earth.jpg
 		
3- java WebBrowser htmldog.com/examples/images1.html

4- java WebBrowser portquiz.net:8080/

5- java WebBrowser www.utdallas.edu/os.html
