import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ScribdCrawler {
    private final WebDriver d;
    ScribdCrawler() {
        ChromeOptions o = new ChromeOptions();
        o.addArguments("--headless=new","--disable-gpu","--no-sandbox");
        d = new ChromeDriver(o);
    }
    List<String> s(String k,int p){
        Set<String> L=new HashSet<>();
        for(int i=1;i<=p;i++){
            d.get("https://www.scribd.com/search?content_type=tops&query="+k+"&page="+i+"&filters=%7B%22date_uploaded%22%3A%223month%22%7D");
            sleep(3000);
            d.findElements(By.cssSelector("a[href*='/document/']"))
             .forEach(e->L.add(e.getAttribute("href").split("\\?")[0]));
        }
        return L.stream().toList();
    }
    boolean c(String u,List<String> t){
        d.get(u);
        sleep(2000);
        String b=d.findElement(By.tagName("body")).getText().toLowerCase();
        return t.stream().anyMatch(x->b.contains(x.toLowerCase()));
    }
    void crawl(List<String> q,List<String> m,int p,Path f) throws Exception{
        try(FileWriter w=new FileWriter(f.toFile(),false)){
            for(String k:q)
                for(String u:s(k,p))
                    w.write((c(u,m)?"": "# no match: ")+u+"\n");
        }finally{d.quit();}
    }
    static void sleep(long ms){try{Thread.sleep(ms);}catch(Exception e){}}
    public static void main(String[] a) throws Exception{
        new ScribdCrawler().crawl(
            List.of(/* INPUT YOUR HEADER TO MATCH  */),
            List.of(/* INPUT YOUR KEYWORDS TO MATCH  */),
            5,                             // pages per term
            Path.of("matched_links.txt")
        );
    }
}
