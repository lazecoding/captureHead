package lazecoding.capture.util;/**
 * 组织分页
 * <p>
 * 使用：
 * PageInfo pageInfo = new PageInfo(2,5,6);
 * int startNum = pageInfo.getStartNum();
 * int endNum = pageInfo.getEndNum();
 *
 * @author lazecoding
 */
public class PageInfo {
    /**
     * 开始的页数偏移量
     */
    private int startNum = 0;

    /**
     * 结束的页数偏移量
     */
    private int endNum = 0;
    /**
     * 第 N 页
     */
    private int pageNum = 0;

    /**
     * 每页数量条数
     */
    private int pageSize = 0;

    /**
     * 记录总数
     */
    private int totolNum = 0;

    /**
     * 最大页数
     */
    private int maxPageNum = 0;

    /**
     * 默认分页数量：15
     */
    private final static int DEFAULT_PAGE_SIZE = 15;

    /**
     * 最小分页数量：1
     */
    private final static int MIN_PAGE_SIZE = 1;

    /**
     * 最大分页数量：50
     */
    private final static int MAX_PAGE_SIZE = 50;

    /**
     * 获取分页信息
     *
     * 区间： (startNum,endNum]
     *
     * @param pageNum  第 N 页
     * @param pageSize 每页数量条数
     * @param totolNum 记录总数
     */
    public PageInfo(int pageNum, int pageSize, int totolNum) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (totolNum < 1) {
            return;
        }
        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        int startNum = (pageNum - 1) * pageSize;
        int endNum = pageNum * pageSize;
        int maxPageNum = (totolNum + pageSize - 1) / pageSize;

        if (startNum > totolNum) {
            startNum = (maxPageNum - 1) * pageSize;
            pageNum = maxPageNum;
        }

        if (endNum > totolNum) {
            endNum = totolNum;
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totolNum = totolNum;
        this.startNum = startNum;
        this.endNum = endNum;
        this.maxPageNum = maxPageNum;
    }

    public int getStartNum() {
        return startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotolNum() {
        return totolNum;
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "startNum=" + startNum +
                ", endNum=" + endNum +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", totolNum=" + totolNum +
                ", maxPageNum=" + maxPageNum +
                '}';
    }
}