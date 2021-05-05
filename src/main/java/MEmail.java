import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MEmail {

    private String host;
    private String pop3;
    private String subject;
    private String folder;
    private boolean debug;
    private boolean showFolder;
    private boolean showHeader;
    private int nMessages;
    private int mode;
    private int time;
    private String username = "";
    private String password = "";

    public MEmail(String username, String password) {

        if(username == null || username == "" || password == null || password == "")
            throw new RuntimeException("Informe seu usuario e senha corretamente");

        host = "imap.gmail.com";
        nMessages = 10;
        pop3 = "pop3";
        debug = false;
        showFolder = false;
        showHeader = true;
        subject = "assunto xxxxxx";
        folder = "INBOX";
        mode = Folder.READ_ONLY;
        time = 10_000;
        this.username = username;
        this.password = password;
    }

    public String getToken() {

        System.out.println("Buscando e-mail..."+ DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
        String token = "";

        try {

            //Seta as propriedades
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "imaps");

            //Configuração padrao
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");

            //Tenta conexão
            store.connect(host, username, password);

            //Mostra debug, padrão false
            emailSession.setDebug(debug);

            //Mostra as pastas
            showFolders(store);

            //Busca pela pasta informada em folder (pardão caixa de entrada - INBOX)
            Folder emailFolder = store.getFolder(folder);

            //Modo de abertura (padrão - leitura)
            emailFolder.open(mode);

            //Busca todas as mensagens
            Message[] messages = emailFolder.getMessages();

            //Busca as ultimas nMensagens (padrão ultimas 10)
            int countLimit = messages.length - nMessages;

            //Faz a busca pelas mensagens
            forMessages:
            for (int i = messages.length - 1; i > countLimit; i--) {
                Message message = messages[i];

                //Busca pelo assunto pré configurado (email porto)
                if (message.getSubject().contains(subject)) {

                    //Mostra ou não o cabeçalho com informações de configurações (padrão true)
                    showHeader(emailFolder, i, message);

                    //Busca mensagens HTML
                    if (message.getContent() instanceof MimeMultipart) {

                        Multipart mp = (Multipart) message.getContent();

                        //Faz a leitura do conteudo
                        forContent:
                        for (int j = 0; j < mp.getCount(); j++) {

                            Part bp = mp.getBodyPart(j);
                            if (bp.isMimeType("text/html")) {
                                String html = (String) bp.getContent();
                                Document document = Jsoup.parse(html);
                                Element link = document.select("div:contains(PIN)").select("p > strong").first();
                                token = link.text();
                                break forMessages;
                            }
                        }
                    }
                }
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return token;

    }

    private void showHeader(Folder emailFolder, int i, Message message) throws MessagingException {
        if(showHeader){
            System.out.println("---------------------------------");
            System.out.println("Total de Mensagens: " + emailFolder.getMessageCount());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Email Number " + i);
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("---------------------------------");
            System.out.println("Configurações: ");
            System.out.println("Host: "+host);
            System.out.println("Busca ultimos ("+nMessages+") emails");
            System.out.println("Mostra debug: "+debug);
            System.out.println("Mostra Todas as pastas: "+showFolder);
            System.out.println("Pasta buscada: "+folder);
            System.out.println("Mostra Todas cabeçalho: "+showHeader);
            System.out.println("Assunto procurado filtrado: "+subject);

            if(mode == 1) System.out.println("Modo de busca das mensagens: (leitura)");
            if(mode == 2) System.out.println("Modo de busca das mensagens: (leitura e escrita)");

            System.out.println("Intervalo de tempo de busca padrão: "+(time/1000)+ " segundos");
            System.out.println("Usuario: "+username);
        }
    }

    private void showFolders(Store store) throws MessagingException {
        if(showFolder){
            Folder[] f = store.getDefaultFolder().list("*");
            for (Folder fd : f) {
                System.out.println(">> " + fd.getName());
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPop3() {
        return pop3;
    }

    public void setPop3(String pop3) {
        this.pop3 = pop3;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getnMessages() {
        return nMessages;
    }

    public void setnMessages(int nMessages) {
        this.nMessages = nMessages;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isShowFolder() {
        return showFolder;
    }

    public void setShowFolder(boolean showFolder) {
        this.showFolder = showFolder;
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int timeInSeconds) {
        if(timeInSeconds == 0) timeInSeconds = 1;
        this.time = timeInSeconds * 1000;
    }
}
