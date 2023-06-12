import api.Currency;
import api.mono.MonoService;
import api.privat.PrivatService;

public class Main {
    public static void main(String[] args) {
        MonoService monoService=new MonoService();
        PrivatService privatService=new PrivatService();
       System.out.println("Mono 1$ - "+monoService.convertorToUAH(Currency.USD, 1));
        System.out.println("Privat 1$ - "+privatService.convertorToUAH(Currency.USD, 1));

    }
}
