package ekstra;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class App
{
    public static void main( String[] args ) throws IOException, ParseException {
        App.run();
    }

    private static void run() throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        File path = new File("index");
        Directory index = new SimpleFSDirectory(path.toPath());
        Scanner input = new Scanner(System.in);

//Generating indexes

//        try (ItemProvider provider = new ItemProvider("items.xml")) {
//            IndexWriterConfig config = new IndexWriterConfig(analyzer);
//            IndexWriter w = new IndexWriter(index, config);
//            while (provider.hasNext()) {
//                Item item = provider.next();
//                addDoc(w, item);
//            }
//            w.close();
//        } catch (XMLStreamException | IOException ex) {
//            ex.printStackTrace();
//        }

        int hits = 50;
        String wordA, wordB, words;
        while(true){
            System.out.println("Query options:\n" +
                            "a) Word A with no word B in 'name'\n" +
                            "b) Word A in 'name', multiple words in 'description'\n" +
                            "c) Word starting with a LETTER in 'category'\n" +
                            "d) Similar words (max 2 different letters)\n" +
                            "e) Sorted items and price is in range from low to high");
            String statement = input.nextLine();
            switch(statement){
                case "a":
                    System.out.println("Word A without word B in 'name'\nWordA:");
                    wordA = input.nextLine();
                    System.out.println("WordB:");
                    wordB = input.nextLine();
                    Query aQ = new QueryParser("name", analyzer).parse("name: " + wordA +" -" + wordB);
                    searchQuery(index, aQ, hits);
                    break;
                case "b":
                    System.out.println("Word A in 'name', words B in 'description'");
                    System.out.println("How many words do you want to search in  'description'?");
                    Integer howMany = Integer.getInteger(input.nextLine());
                    System.out.println("WordA:");
                    wordA = input.nextLine();
                    System.out.println("Words: ");
                    words = input.nextLine();
                    for(int i=howMany-1; i<0; i--){
                        words += " OR " + input.nextLine();
                    }
                    Query bQ = new QueryParser("name", analyzer).parse("+name: "+ wordA +" +description: ("+ words +")");
                    searchQuery(index, bQ, hits);
                    break;
                case "c":
                    System.out.println("Word starting with a LETTER in 'category'");
                    System.out.println("A letter:");
                    String cA = input.nextLine();
                    Query cQ = new QueryParser("category", analyzer).parse("+category: "+ cA +"*");
                    searchQuery(index, cQ, hits);
                    break;
                case "d":
                    System.out.println("Similar words (max 2 different letters)");
                    System.out.println("A letter:");
                    wordA = input.nextLine();
                    Query dQ = new FuzzyQuery(new Term("name", wordA), 2);
                    searchQuery(index, dQ, hits);
                    break;
                case "e":
                    System.out.println("Sorted items and price is in range from low to high");
                    System.out.println("Lower value:");
                    Integer lower = Integer.parseInt(input.nextLine());
                    System.out.println("Higher value:");
                    Integer higher =  Integer.parseInt(input.nextLine());
                    Query eQ = SortedNumericDocValuesField.newSlowRangeQuery("price", lower, higher);
                    searchQuery(index, eQ, hits);
                    break;
                default:
                    System.out.println("Wrong char");
                    break;
            }
        }
    }

    private static void searchQuery(Directory index, Query q, int hpp) throws IOException {
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        Sort sort = new Sort(new SortedNumericSortField("price", SortField.Type.LONG));

        TopDocs docs = searcher.search(q, hpp, sort);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i+1) + ". " + d.get("name") + "\n"+
                    "Price: " + d.get("price") + " Category: " + d.get("category") + "\n"
                    +  d.get("description") + "\n"
            );
        }
    }


    private static void addDoc(IndexWriter w, Item item) throws IOException {
        Document doc = new Document();
        String idStr = String.valueOf(item.getId());
        int priceInt = Math.round(item.getPrice());
        long priceLong = (long) priceInt;

        doc.add(new StringField("id", idStr, Field.Store.YES));
        doc.add(new TextField("name", item.getName(), Field.Store.YES));
        doc.add(new TextField("category", item.getCategory(), Field.Store.YES));
        doc.add(new TextField("description", item.getDescription(), Field.Store.YES));
        doc.add(new SortedNumericDocValuesField("price", priceLong));
        doc.add(new StoredField("price", Math.round(item.getPrice())));
        w.addDocument(doc);
    }

}
