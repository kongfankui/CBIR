package image_processing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetRecordCount {
    private int count;

	public int getCount() throws SQLException {
		setCount();
		return count;
	}

	public void setCount() throws SQLException {
		Connection con=DBUtil.getConnection();
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery("select imageID from image_library");
	    rs.last();
	    count=rs.getRow();
	}
    
}
