package general;

public class Protocol {

    /**
     * Protocol
     */

    public static final int VERSION_NO = 1;

    public static class Client {
        /**
         * Things that client can send to the server.
         * Handled by server according protocol.
         */

        public static final String LIST = "LIST";
        public static final String UPLOAD = "UPLOAD";
        public static final String DOWNLOAD = "DOWNLOAD";

    }

    public static class Server {


    }

    public static class General {
        public static final String DELIMITER1 = "$";
    }

}
