import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Principal {

    public static void main(String[] args) throws InterruptedException {


        MEmail mEmail = new MEmail("xxxxxxxxxxxxxxx@gmail.com", "xxxxxxxxxxxxxxxxxxxxxxx");
//        mEmail.setDebug(true); //padrão false
//        mEmail.setShowFolder(true); //padrão false
        mEmail.setShowHeader(false); //padrão true
        mEmail.setnMessages(5); // padrão 10 ultimas mensagens
        mEmail.setTime(3); //padrão 10 segundos

        //Exemplo de busca a cada 3 segundos
        while(true){
            String token = mEmail.getToken();
            if(token != null && !token.equals("")){
                System.err.println("Token recuperado: "+token);
            }
            Thread.sleep(mEmail.getTime());
        }
    }

}
