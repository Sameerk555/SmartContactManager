console.log("this is script file")

const toggleSidebar=()=>{
	
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display", "none")
		$(".content").css("margin-left", "0%")
	}else{
		$(".sidebar").css("display", "block")
		$(".content").css("margin-left", "20%")
	}
};
const search=()=>{

	// console.log("called")
	let query=("#search-input").valueOf();
	console.log(query);

	if(query==""){
		$(".search-result").hide();
	}
	else{  
		console.log(query); 
		
		let url= `http://localhost:8181/search/${query}`;

		fetch(url).then((response)=>{
			return response.json();
		}).then(data=>{
			console.log(data);
		});
		 
		$(".search-result").show();  
	}  

}