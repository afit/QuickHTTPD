QuickHTTPD: Flexible service middlware
======================================

What is it?
-----------

QuickHTTPD is a piece of middleware. It is Open Source, backed by a DTD-based XML configuration structure and has been developed with efficiency and flexibility in mind. It is a pure Java application with no external dependencies and can be run on any platform that provides a 1.4 JVM (for a C# .NET 2.0 implementation of this technology, see the `LPC <http://www.reincubate.com/labs/lothianproductionscommon-action-caching-and-brokering-framework>`_).

It is an open mechanism that can be easily adapted to service requests from a variety of services, to send appropriate loosely-tied responses from customisable document sources and to perform arbitrary processing based on the requests. In this regard "QuickHTTPD" is a misnomer.

Out of the box, the QuickHTTPD can be used to provide:

* An embedded web-server with on-the-fly compression or decompression capabilities
* A dedicated web service
* A dedicated web application
* A thin web-server to act as a façade for a complex web application
* A limited proxy server
* Sample code for developers interested in building a web server

QuickHTTPD is only a framework, and as a consequence when customized it probably won't be as agile as a dedicated purpose-built component. It may only be of value to your organisation for prototyping.

It is used successfully by Lothian Productions to form the backbone of a caching system.

How does it work?
-----------------

The provided API defines interfaces for RequestListeners, RequestHandlers and DocumentSources. A RequestListener listens for an incoming request (usually on a given port) and passes on any request it gets to a RequestHandler. The handler interprets the request, converting it into a document request. The document request is then sent to a DocumentSource which returns a document to be streamed back to the requestor.

The simplest implementation works as follows:

+-----------------------------+----------------------------------------------------------------------------------------------------+
| ServetSocketRequestListener | Listens on a port specified in the XML configuration for incoming requests                         |
+-----------------------------+----------------------------------------------------------------------------------------------------+
| HTTPRequestHandler          | Interprets the request data as an HTTP request                                                     |
+-----------------------------+----------------------------------------------------------------------------------------------------+
| FilesystemDocumentSource    | Serves the requested document from the server's file system, as specified by the XML configuration |
+-----------------------------+----------------------------------------------------------------------------------------------------+

Future goals for QuickHTTPD
---------------------------

It is only partially complete, although it's fully functional for the purposes described above. A number of incomplete refactorings are marked with FIXME comments in the code.

Goals include:

* Adding JMS listeners and sources
* Improving the logging format and mechanisms (it's currently in common Apache format)

Where can I get it?
-------------------

 From this GitHub. The latest code is 0.9, dating back to 29th November 2003