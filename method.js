function Sample1(){
        var hoge = Sample1_form.search_name.value;

        alert(""+ hoge +"が入力されました。");
    }
//alert("Hello World!");

function test(){
  var lower = 0; //初期化
  var upper = 0; //初期化
  lower = DetailedSearch_form.search_lowerReputation.value;
  upper = DetailedSearch_form.search_upperReputation.value;
	if (Number(lower) > Number(upper)){
		alert("入力に誤りがあります");
		return false;
	}else{
		return true;
	}
}
