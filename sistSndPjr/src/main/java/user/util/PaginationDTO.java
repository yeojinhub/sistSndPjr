package user.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PaginationDTO {

	private int pageNumber, currentPage, totalPage;
	private String url, field, keyword, route, elect, hydro, wash, repair, truck;
	
}// class