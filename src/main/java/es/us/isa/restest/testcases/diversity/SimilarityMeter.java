package es.us.isa.restest.testcases.diversity;

import es.us.isa.restest.testcases.TestCase;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.checkerframework.checker.units.qual.C;


public class SimilarityMeter {

    private METRIC similarityMetric;
    private JaccardSimilarity jaccardMeter;
    private JaroWinklerSimilarity jaroWinklerMeter;
    private LevenshteinDistance levenshteinMeter;
    private LongestCommonSubsequence longestCommonSubsequencesMeter;
    private ContentMeter contentMeter;


    public SimilarityMeter(METRIC similarityMetric) {
        this.similarityMetric = similarityMetric;

        switch (similarityMetric) {
            case JACCARD:
                jaccardMeter = new JaccardSimilarity();
                break;
            case JARO_WINKLER:
                jaroWinklerMeter = new JaroWinklerSimilarity();
                break;
            case LEVENSHTEIN:
                levenshteinMeter = new LevenshteinDistance();
                break;
            case LONGEST_COMMON_SUBSEQUENCE:
                longestCommonSubsequencesMeter = new LongestCommonSubsequence();
                break;
            case CONTENT:
                contentMeter = new ContentMeter();
                break;
            default:
                throw new IllegalArgumentException("The similarity metric " + similarityMetric + " is not supported");
        }
    }

/** If it needs more distance calculating method, we need more similarityMetric
the calculating method detail is in the xxxMeter.apply() method
the xxxMeter class implements the interface SimilarityScore<Double>
 <p>返回值越小说明相似度约小</p>
 */
    public Double apply (TestCase left, TestCase right) {
        switch (similarityMetric) {
            case JACCARD:
                return jaccardMeter.apply(left.getFlatRepresentation(), right.getFlatRepresentation());
            case JARO_WINKLER:
                return jaroWinklerMeter.apply(left.getFlatRepresentation(), right.getFlatRepresentation());
            case LEVENSHTEIN: // Computed as: 1 - (distance / maxlength(left, right))
                double maxStringLength = Math.max(left.getFlatRepresentation().length(), right.getFlatRepresentation().length());
                if (maxStringLength != 0)
                    return 1 - (double)levenshteinMeter.apply(left.getFlatRepresentation(), right.getFlatRepresentation()) / maxStringLength;
                else
                    return 1d;
            case LONGEST_COMMON_SUBSEQUENCE:
                return left.getFlatRepresentation().length() + right.getFlatRepresentation().length()
                        - 2 * longestCommonSubsequencesMeter.apply(left.getFlatRepresentation(), right.getFlatRepresentation()) * 1d;
            case CONTENT:
                return contentMeter.apply(left, right);
            default:
                throw new IllegalArgumentException("The similarity metric " + similarityMetric + " is not supported");
        }
    }

    public METRIC getSimilarityMetric() {
        return similarityMetric;
    }

/**Here to defind more metric name
 * they all calculate with two CharSequence parameters, and return double
 * <p>JACCARD: 直接把两个string拆成char，放进hashset，再生成union和intersection的两个set，返回inter.size()/union.size()</p>
 *
 * <p>JARO_WINKLER:jaro-winkler字符匹配算法
 * <a href = "http://t.csdn.cn/wJaH0">http://t.csdn.cn/wJaH0</a></p>
 *
 * <p>LEVENSHTEIN:两个字串之间，由一个转换成另一个所需的最少编辑操作次数，允许插、删、换
 * <a href = "http://t.csdn.cn/Dzmhb">http://t.csdn.cn/Dzmhb</a></p>
 *
 * <p>添加了最长公共子串的判断</p>
 */
    public enum METRIC {
        JACCARD,
        JARO_WINKLER,
        LEVENSHTEIN,
        LONGEST_COMMON_SUBSEQUENCE,
        CONTENT
    }

}