package com.mitchseymour.ksql.functions;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import hex.genmodel.easy.RowData;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.EasyPredictModelWrapper.Config;
import hex.genmodel.easy.prediction.*;
import hex.genmodel.GenModel;
import hex.genmodel.ModelMojoReader;
import hex.genmodel.MojoReaderBackend;
import hex.genmodel.MojoReaderBackendFactory;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

@UdfDescription(
    name = "predict_spam",
    description = "Predict whether or not an email is spam or ham. Based on Enron email data",
    version = "0.1.0",
    author = "Mitch Seymour"
)
public class PredictSpamUdf {
  private final EasyPredictModelWrapper model = getModel();
  private List<String> features;
  
  // stop words obtained from the tidytext::stop_words function in R
  private final List<String> stopWords = Arrays.asList(new String[]{"a","a's","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","ain't","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren't","around","as","aside","ask","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","c'mon","c's","came","can","can't","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn't","course","currently","d","definitely","described","despite","did","didn't","different","do","does","doesn't","doing","don't","done","down","downwards","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","h","had","hadn't","happens","hardly","has","hasn't","have","haven't","having","he","he's","hello","help","hence","her","here","here's","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i","i'd","i'll","i'm","i've","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","isn't","it","it'd","it'll","it's","its","itself","j","just","k","keep","keeps","kept","know","knows","known","l","last","lately","later","latter","latterly","least","less","lest","let","let's","like","liked","likely","little","look","looking","looks","ltd","m","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","o","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","shouldn't","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","t's","take","taken","tell","tends","th","than","thank","thanks","thanx","that","that's","thats","the","their","theirs","them","themselves","then","thence","there","there's","thereafter","thereby","therefore","therein","theres","thereupon","these","they","they'd","they'll","they're","they've","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","uucp","v","value","various","very","via","viz","vs","w","want","wants","was","wasn't","way","we","we'd","we'll","we're","we've","welcome","well","went","were","weren't","what","what's","whatever","when","whence","whenever","where","where's","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","who's","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","won't","wonder","would","would","wouldn't","x","y","yes","yet","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves","z","zero","i","me","my","myself","we","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","would","should","could","ought","i'm","you're","he's","she's","it's","we're","they're","i've","you've","we've","they've","i'd","you'd","he'd","she'd","we'd","they'd","i'll","you'll","he'll","she'll","we'll","they'll","isn't","aren't","wasn't","weren't","hasn't","haven't","hadn't","doesn't","don't","didn't","won't","wouldn't","shan't","shouldn't","can't","cannot","couldn't","mustn't","let's","that's","who's","what's","here's","there's","when's","where's","why's","how's","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very","a","about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","best","better","between","big","both","but","by","came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could","did","differ","different","differently","do","does","done","down","down","downed","downing","downs","during","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","had","has","have","having","he","her","here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if","important","in","interest","interested","interesting","interests","into","is","it","its","itself","just","keep","keeps","kind","knew","know","known","knows","large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my","myself","necessary","need","needed","needing","needs","never","new","new","newer","newest","next","no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","our","out","over","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","quite","rather","really","right","right","room","rooms","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows","side","sides","since","small","smaller","smallest","some","somebody","someone","something","somewhere","state","states","still","still","such","sure","take","taken","than","that","the","their","them","then","there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","under","until","up","upon","us","use","used","uses","very","want","wanted","wanting","wants","was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who","whole","whose","why","will","with","within","without","work","worked","working","works","would","year","years","yet","you","young","younger","youngest","your","yours"});
  
  private EasyPredictModelWrapper getModel() {
    try {
      // load the embedded model from the sources directory
      URL mojoURL = getClass().getClassLoader().getResource("GLM_model_R_1555097962289_108.zip");
      MojoReaderBackend reader = MojoReaderBackendFactory.createReaderBackend(mojoURL, MojoReaderBackendFactory.CachingStrategy.MEMORY);
      GenModel model = ModelMojoReader.readFrom(reader);

      // configure the model
      final Config modelConfig = new EasyPredictModelWrapper.Config()
        .setModel(model)
        .setConvertUnknownCategoricalLevelsToNa(true);

      // save the feature names so we can use them to build predictions later
      features = Arrays.asList(modelConfig.getModel().getNames());
      return new EasyPredictModelWrapper(modelConfig);
    } catch (Exception e) {
      throw new RuntimeException("Error building spam predicton model", e);
    }
  }
  @Udf(description = "Predict ham or spam")
  public String predict(String text) {
    try {
      List<String> words = Arrays.asList(text.split("\\s+"));

      // convert words to lowercase, alphanumeric strings
      List<String> wordsFiltered = words.stream()
        .map(String::toLowerCase)
        .map((s) ->  s.trim().replaceAll("[^a-zA-Z0-9]", ""))
        .filter((s) -> s.length() > 0)
        .collect(Collectors.toList());

      // count the number of stop words in the text
      int numberStopWords = 0;
      for (String word : wordsFiltered) {
        if (stopWords.contains(word)) {
          numberStopWords++;
          continue;
        }
      }

      // add the features to the row
      RowData row = new RowData();
      for (String feature : features) {
        if (wordsFiltered.contains(feature)) {
          final double current = (double) row.getOrDefault(feature, 0.0);
          row.put(feature, current+1);
        } else {
          row.put(feature, 0.0);
        }
      }
      row.put("number_words", (double)  words.size());
      row.put("number_characters", (double)  text.length());
      row.put("number_stop_words", (double) numberStopWords);
      row.put("proportion_stop_words", (double) numberStopWords / (double) words.size());

      // make a prediction
      BinomialModelPrediction p = model.predictBinomial(row);

      // return the result. note: for some reason, p.label doesn't resolve to the correct value
      if (p.classProbabilities[0] > p.classProbabilities[1]) {
        return "ham";
      }
      return "spam";
    } catch (Exception e) {
      return "unknown";
    }
  }
}
