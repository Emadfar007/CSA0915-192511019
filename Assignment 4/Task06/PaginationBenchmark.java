import java.util.ArrayList;
import java.util.List;

public class PaginationBenchmark {
    private record Row(int id,String value){}
    public static void main(String[] args){
        int total=100_000,pageSize=100;long start=System.nanoTime();List<Row> all=new ArrayList<>(total);for(int i=0;i<total;i++)all.add(new Row(i,"Student-"+i));long allTime=System.nanoTime()-start;
        start=System.nanoTime();List<Row> page=new ArrayList<>(pageSize);for(int i=0;i<pageSize;i++)page.add(new Row(i,"Student-"+i));long pageTime=System.nanoTime()-start;
        long estimatedAllBytes=(long)total*64,estimatedPageBytes=(long)pageSize*64;
        System.out.println("strategy,rows,build_nanos,estimated_bytes");System.out.println("load_all,"+all.size()+","+allTime+","+estimatedAllBytes);System.out.println("page,"+page.size()+","+pageTime+","+estimatedPageBytes);
        System.out.println("memory_reduction="+(estimatedAllBytes/estimatedPageBytes)+"x");
    }
}
