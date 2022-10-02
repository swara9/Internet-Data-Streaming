public class TableEntry {
    int flowID;
    int count;

    public TableEntry(int flowID){
        this.flowID = flowID;
        count = 1;
    }

    @Override
    public String toString() {
        return "flowId = "+flowID+", count= "+count;
    }
}
