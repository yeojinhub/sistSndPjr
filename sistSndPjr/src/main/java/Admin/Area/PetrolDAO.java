package Admin.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DBConnection.DBConnection;
import Pagination.PaginationDTO;

public class PetrolDAO {

	private static PetrolDAO petDTO;
	
	private PetrolDAO() {
		
	} //PetrolDAO
	
	public static PetrolDAO getInstance() {
		if( petDTO == null ) {
			petDTO = new PetrolDAO();
		} //end if
		
		return petDTO;
	} //getInstance
	
	/**
	 * 전체 주유소 수를 조회합니다.
	 * @return 전체 주유소 수
	 * @throws SQLException 예외처리
	 */
	public int getTotalPetrolCount() throws SQLException {
		int totalCount = 0;
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			con = dbCon.getDbCon();
			
			StringBuilder countQuery = new StringBuilder();
			countQuery
			.append("	SELECT COUNT(*) as total_count	")
			.append("	from petrol	")
			;
			
			pstmt = con.prepareStatement(countQuery.toString());
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				totalCount = rs.getInt("total_count");
			} //end if
			
		} finally {
			dbCon.dbClose(con, pstmt, rs);
		} //end try finally
		
		return totalCount;
	} //getTotalPetrolCount
	
	/**
	 * 페이지네이션을 적용하여 주유소 목록을 조회합니다. (Oracle 11g 이하 - ROWNUM 사용)
	 * @param pagination 페이지네이션 정보
	 * @return 페이지에 해당하는 주유소 목록
	 * @throws SQLException 예외처리
	 */
	public List<PetrolDTO> selectPetrolsByPage(PaginationDTO pagination) throws SQLException {
		List<PetrolDTO> petList = new ArrayList<PetrolDTO>();
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			con = dbCon.getDbCon();
			
			StringBuilder selectPageQuery = new StringBuilder();
			selectPageQuery
			.append("	SELECT * FROM (	")
			.append("		SELECT ROWNUM as rnum, pet_num, name, route,	")
			.append("		gasoline, diesel, lpg, elect, hydro	")
			.append("		FROM (	")
			.append("			select	pet_num, name, route,	")
			.append("			gasoline, diesel, lpg, elect, hydro	")
			.append("			FROM petrol	")
			.append("			ORDER BY route ASC, pet_num DESC	")
			.append("		) WHERE ROWNUM <= ?	")
			.append("	) WHERE rnum >= ?	")
			;
			
			pstmt = con.prepareStatement(selectPageQuery.toString());
			pstmt.setInt(1, pagination.getEndRowNum());
			pstmt.setInt(2, pagination.getStartRowNum());
			
			rs = pstmt.executeQuery();
			
			PetrolDTO petDTO = null;
			
			while( rs.next() ) {
				petDTO = new PetrolDTO();
				petDTO.setPetNum(rs.getInt("pet_num"));
				petDTO.setAreaName(rs.getString("name"));
				petDTO.setAreaRoute(rs.getString("route"));
				petDTO.setGasoline(rs.getString("gasoline"));
				petDTO.setDiesel(rs.getString("diesel"));
				petDTO.setLpg(rs.getString("lpg"));
				petDTO.setElect(rs.getString("elect"));
				petDTO.setHydro(rs.getString("hydro"));
				
				petList.add(petDTO);
			} //end while
			
		} finally {
			dbCon.dbClose(con, pstmt, rs);
		} //end try finally
		
		return petList;
	} //selectPetrolsByPage
	
	/**
	 * 검색 조건에 따른 주유소 목록을 페이지네이션으로 조회합니다. (Oracle 11g 이하 - ROWNUM 사용)
	 * @param searchType 검색 유형 (name, email, tel)
	 * @param searchKeyword 검색어
	 * @param pagination 페이지네이션 정보
	 * @return 검색 조건과 페이지에 해당하는 주유소 목록
	 * @throws SQLException 예외처리
	 */
	public List<PetrolDTO> searchPetrolsByPage(String searchType, String searchKeyword, PaginationDTO pagination) throws SQLException {
		List<PetrolDTO> petList = new ArrayList<PetrolDTO>();
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			con = dbCon.getDbCon();
			
			StringBuilder searchQuery = new StringBuilder();
			searchQuery
			.append("	SELECT * FROM (	")
			.append("		select ROWNUM as rnum, pet_num, name, route,	")
			.append("		gasoline, diesel, lpg, elect, hydro	")
			.append("		from (	")
			.append("			select	pet_num, name, route,	")
			.append("			gasoline, diesel, lpg, elect, hydro	")
			.append("			from	petrol	")
			;
			
			// 검색어가 null이 아닌 경우에만 조건 추가 (PaginationUtil에서 이미 처리됨)
			if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
				switch (searchType) {
					case "name":
						searchQuery.append("	where name LIKE ?	");
						break;
					case "route":
						searchQuery.append("	where route LIKE ?	");
						break;
				} //end switch
			} //end if
			
			searchQuery
			.append("			order by route	")
			.append("		) WHERE ROWNUM <= ?	")
			.append("	) WHERE rnum >= ?	");
			
			pstmt = con.prepareStatement(searchQuery.toString());
			
			int paramIndex = 1;
			if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
				pstmt.setString(paramIndex++, "%" + searchKeyword + "%");
			} //end if
			pstmt.setInt(paramIndex++, pagination.getEndRowNum());
			pstmt.setInt(paramIndex, pagination.getStartRowNum());
			
			rs = pstmt.executeQuery();
			
			PetrolDTO petDTO = null;
			while( rs.next() ) {
				petDTO = new PetrolDTO();
				petDTO.setPetNum(rs.getInt("pet_num"));
				petDTO.setAreaName(rs.getString("name"));
				petDTO.setAreaRoute(rs.getString("route"));
				petDTO.setGasoline(rs.getString("gasoline"));
				petDTO.setDiesel(rs.getString("diesel"));
				petDTO.setLpg(rs.getString("lpg"));
				petDTO.setElect(rs.getString("elect"));
				petDTO.setHydro(rs.getString("hydro"));
				
				petList.add(petDTO);
			} //end while
			
		} finally {
			dbCon.dbClose(con, pstmt, rs);
		} //end try finally
		
		return petList;
	} //searchPetrolsByPage
	
	/**
	 * 검색 조건에 따른 주유소 수를 조회합니다.
	 * @param searchType 검색 유형 (route, name)
	 * @param searchKeyword 검색어
	 * @return 검색 조건에 맞는 주유소 수
	 * @throws SQLException 예외처리
	 */
	public int getSearchPetrolCount(String searchType, String searchKeyword) throws SQLException {
	    int totalCount = 0;
	    
	    DBConnection dbCon = DBConnection.getInstance();
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    Connection con = null;
	    
	    try {
	        con = dbCon.getDbCon();
	        
	        StringBuilder countQuery = new StringBuilder();
	        countQuery
	            .append("SELECT COUNT(*) AS total_count ")  // PETROL 기준 카운트
	            .append("FROM petrol ")
	            ;

	        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
	            switch (searchType) {
	                case "name":
	                    countQuery.append("WHERE name LIKE ? ");  // AREA 테이블 컬럼 사용
	                    break;
	                case "route":
	                    countQuery.append("WHERE route LIKE ? ");  // AREA 테이블 컬럼 사용
	                    break;
	            }
	        }

	        pstmt = con.prepareStatement(countQuery.toString());

	        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
	            String keyword = searchKeyword.trim();
	            pstmt.setString(1, "%" + keyword + "%");
	        }
	        
	        rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            totalCount = rs.getInt("total_count");
	        }
	    } finally {
	        dbCon.dbClose(con, pstmt, rs);
	    }
	    
	    return totalCount;
	}
	
	/**
	 * 검색 조건에 따른 주유소 목록을 페이지네이션으로 조회합니다. (Oracle 12c 이상)
	 * @param searchType 검색 유형 (name, email, tel)
	 * @param searchKeyword 검색어
	 * @param pagination 페이지네이션 정보
	 * @return 검색 조건과 페이지에 해당하는 주유소 목록
	 * @throws SQLException 예외처리
	 */
	public List<PetrolDTO> searchPetrolsByPageOracle12c(String searchType, String searchKeyword, PaginationDTO pagination) throws SQLException {
		List<PetrolDTO> petList = new ArrayList<PetrolDTO>();
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			con = dbCon.getDbCon();
			
			StringBuilder searchQuery = new StringBuilder();
			searchQuery
			.append("	select	pet_num, name, route,	")
			.append("	gasoline, diesel, lpg, elect, hydro	")
			.append("	from	petrol	")
			.append("	order by route	")
			;
			
			// 검색어가 null이 아닌 경우에만 조건 추가 (PaginationUtil에서 이미 처리됨)
			if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
				switch (searchType) {
					case "name":
						searchQuery.append("	where name LIKE ?	");
						break;
					case "route":
						searchQuery.append("	where route LIKE ?	");
						break;
				} //end switch
			} //end if
			
			searchQuery.append("	ORDER BY route	");
			searchQuery.append("	OFFSET ? ROWS FETCH NEXT ? ROWS ONLY	");
			
			pstmt = con.prepareStatement(searchQuery.toString());
			
			int paramIndex = 1;
			if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
				pstmt.setString(paramIndex++, "%" + searchKeyword + "%");
			} //end if
			pstmt.setInt(paramIndex++, pagination.getOffset());
			pstmt.setInt(paramIndex++, pagination.getPageSize());
			
			rs = pstmt.executeQuery();
			
			PetrolDTO petDTO = null;
			while( rs.next() ) {
				petDTO = new PetrolDTO();
				petDTO.setPetNum(rs.getInt("pet_num"));
				petDTO.setAreaName(rs.getString("name"));
				petDTO.setAreaRoute(rs.getString("route"));
				petDTO.setGasoline(rs.getString("gasoline"));
				petDTO.setDiesel(rs.getString("diesel"));
				petDTO.setLpg(rs.getString("lpg"));
				petDTO.setElect(rs.getString("elect"));
				petDTO.setHydro(rs.getString("hydro"));
				
				petList.add(petDTO);
			} //end while
			
		} finally {
			dbCon.dbClose(con, pstmt, rs);
		} //end try finally
		
		return petList;
	} //searchPetrolsByPageOracle12c
	
	/**
	 * 전체 주유소 조회
	 * @return petList 전체 주유소 리스트
	 * @throws SQLException 예외처리
	 */
	public List<PetrolDTO> selectAllPetrol() throws SQLException {
		List<PetrolDTO> petList = new ArrayList<PetrolDTO>();
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			// 1. JNDI 사용객체 생성.
			// 2. DBCP에서 연결객체 얻기(DataSource).
			// 3. Connection 얻기.
			con = dbCon.getDbCon();

			// 4. 쿼리문 생성객체 얻기.
			StringBuilder selectAllQuery = new StringBuilder();
			selectAllQuery
			.append("	select	pet_num, name, route,	")
			.append("	gasoline, diesel, lpg, elect, hydro	")
			.append("	from	petrol	")
			.append("	order by route	")
			;
			
			pstmt = con.prepareStatement(selectAllQuery.toString());

			// 5. bind 변수에 값 할당
			// 6. 쿼리문 수행 후 결과 얻기.
			rs = pstmt.executeQuery();

			PetrolDTO petDTO = null;

			while (rs.next()) {
				petDTO = new PetrolDTO();
				petDTO.setPetNum(rs.getInt("pet_num"));
				petDTO.setAreaName(rs.getString("name"));
				petDTO.setAreaRoute(rs.getString("route"));
				petDTO.setGasoline(rs.getString("gasoline"));
				petDTO.setDiesel(rs.getString("diesel"));
				petDTO.setLpg(rs.getString("lpg"));
				petDTO.setElect(rs.getString("elect"));
				petDTO.setHydro(rs.getString("hydro"));

				petList.add(petDTO);
			} // end while

		} finally {
			// 7. 연결 끊기.
			dbCon.dbClose(con, pstmt, rs);
		} // end try finally
		
		return petList;
	} //selectAllPetrol
	
	/**
	 * 단일 주유소 조회
	 * @param num 조회할 주유소 번호
	 * @return petDTO 단일 주유소 상세정보
	 * @throws SQLException 예외처리
	 */
	public PetrolDTO selectOnePetrol(int num) throws SQLException {
		System.out.println("DAO에서의 조회할 주유소 번호 : "+num);
		
		PetrolDTO petDTO = null;
		
		DBConnection dbCon = DBConnection.getInstance();

		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;

		try {
			// 1. JNDI 사용객체 생성.
			// 2. DBCP에서 연결객체 얻기(DataSource).
			// 3. Connection 얻기.
			con = dbCon.getDbCon();

			// 4. 쿼리문 생성객체 얻기.
			StringBuilder selectOneQuery = new StringBuilder();
			selectOneQuery
			.append("	select	pet_num, name, route, tel,	")
			.append("	gasoline, diesel, lpg, elect, hydro	")
			.append("	from	petrol	")
			.append("	where	pet_num=?	")
			;
			System.out.println("DAO에서의 실행할 쿼리문 :"+selectOneQuery);
			
			pstmt = con.prepareStatement(selectOneQuery.toString());

			// 5. bind 변수에 값 할당
			pstmt.setInt(1, num);
			
			// 6. 쿼리문 수행 후 결과 얻기.
			rs = pstmt.executeQuery();

			while (rs.next()) {
				petDTO = new PetrolDTO();
				petDTO.setPetNum(rs.getInt("pet_num"));
				petDTO.setAreaName(rs.getString("name"));;
				petDTO.setAreaRoute(rs.getString("route"));
				petDTO.setAreaTel(rs.getString("tel"));
				petDTO.setGasoline(rs.getString("gasoline"));
				petDTO.setDiesel(rs.getString("diesel"));
				petDTO.setLpg(rs.getString("lpg"));
				petDTO.setElect(rs.getString("elect"));
				petDTO.setHydro(rs.getString("hydro"));
			} // end while
			System.out.println("DAO에서의 저장된 dto 값 : "+petDTO);
		} finally {
			// 7. 연결 끊기.
			dbCon.dbClose(con, pstmt, rs);
		} // end try finally
		
		return petDTO;
	} //selectOnePetrol
	
	/**
	 * 주유소 등록
	 * @param petDTO 등록할 주유소 상세정보
	 * @throws SQLException 예외처리
	 */
	public void insertPetrol(PetrolDTO petDTO)  throws SQLException {
		
		DBConnection dbCon = DBConnection.getInstance();
		
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Connection con = null;
		
		try {
			// 1. JNDI 사용객체 생성.
			// 2. DBCP에서 연결객체 얻기(DataSource).
			// 3. Connection 얻기.
			con = dbCon.getDbCon();
			
			// 4. 쿼리문 생성객체 얻기.
			StringBuilder insertQuery = new StringBuilder();
			insertQuery
			.append("	insert	into	petrol	")
			.append("	(pet_num, name, tel, route,	")
			.append("	gasoline, diesel, lpg, elect, hydro)	")
			.append("	values(seq_pet_num.nextval,	")
			.append("	?,?,?,	")
			.append("	?,?,?,?,?)	")
			;
			
			pstmt = con.prepareStatement(insertQuery.toString());
			
			// 5. bind 변수에 값 할당
			pstmt.setString(1, petDTO.getAreaName());
			pstmt.setString(2, petDTO.getAreaTel());
			pstmt.setString(3, petDTO.getAreaRoute());
			pstmt.setString(4, petDTO.getGasoline());
			pstmt.setString(5, petDTO.getDiesel());
			pstmt.setString(6, petDTO.getLpg());
			pstmt.setString(7, petDTO.getElect());
			pstmt.setString(8, petDTO.getHydro());
			
			// 6. 쿼리문 수행 후 결과 얻기.
			rs=pstmt.executeQuery();
			
		} finally {
			// 7. 연결 끊기.
			dbCon.dbClose(con, pstmt, rs);
		} //end try finally
		
	} //insertPetrol
	
	public int updatePetrol(PetrolDTO petDTO) throws SQLException {
		int flagNum = 0;

		DBConnection dbCon = DBConnection.getInstance();

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 1. JNDI 사용객체 생성.
			// 2. DBCP에서 연결객체 얻기(DataSource).
			// 3. Connection 얻기.
			con = dbCon.getDbCon();
			
			// 4. 쿼리문 생성객체 얻기.
			StringBuilder updateQuery = new StringBuilder();
			updateQuery
			.append("	update	petrol	")
			.append("	set gasoline=?, diesel=?, lpg=?, elect=?, hydro=?	")
			.append("	where	pet_num=?	")
			;
			System.out.println("DAO에서의 실행할 쿼리문 :"+updateQuery);

			pstmt = con.prepareStatement(updateQuery.toString());
			
			// 5. bind 변수에 값 할당
			pstmt.setString(1, petDTO.getGasoline());
			pstmt.setString(2, petDTO.getDiesel());
			pstmt.setString(3, petDTO.getLpg());
			pstmt.setString(4, petDTO.getElect());
			pstmt.setString(5, petDTO.getHydro());
			pstmt.setInt(6, petDTO.getPetNum());
			
			System.out.println("DAO에서의 저장된 dto 값 : "+petDTO);
			
			// 6. 쿼리문 수행 후 결과 얻기.
			flagNum = pstmt.executeUpdate();
			
			System.out.println("DAO에서의 실행결과 번호 : "+flagNum);
			
		} finally {
			// 7. 연결 끊기.
			dbCon.dbClose(con, pstmt, null);
		} //end try finally
		
		return flagNum;
	} //updatePetrol
	
	/**
	 * 주유소 삭제
	 * @param petNumList 삭제할 주유소 번호 리스트
	 * @return flagNum 성공시 1, 실패시 0 반환
	 * @throws SQLException 예외처리
	 */
	public int deletePetrol(List<Integer> petNumList) throws SQLException {
		int flagNum = 0;

		DBConnection dbCon = DBConnection.getInstance();

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 1. JNDI 사용객체 생성.
			// 2. DBCP에서 연결객체 얻기(DataSource).
			// 3. Connection 얻기.
			con = dbCon.getDbCon();

			// 4. 쿼리문 생성객체 얻기.
			StringBuilder deleteQuery = new StringBuilder();
			deleteQuery
			.append("	delete	from petrol	")
			.append("	where pet_num in (	")
			;
			
			int count = 0;
			for(int i=0; i<petNumList.size(); i++) {
				if( i == petNumList.size()-1 ) {
					deleteQuery
					.append("	?	")
					;
				} else {
					deleteQuery
					.append("	?,	")
					;
				} //end if else
				count ++;
			} //end for
			
			deleteQuery
			.append("	)	")
			;

			pstmt = con.prepareStatement(deleteQuery.toString());

			// 5. bind 변수에 값 할당
			for(int j=0; j<count; j++) {
				pstmt.setInt(j+1, petNumList.get(j) );
			} //end for

			// 6. 쿼리문 수행 후 결과 얻기.
			flagNum = pstmt.executeUpdate();

		} finally {
			// 7. 연결 끊기.
			dbCon.dbClose(con, pstmt, null);
		} // end try finally

		return flagNum;
	} // deletePetrol
	
} //class