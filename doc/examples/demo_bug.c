int main(){
	unsigned int x = 0;
	unsigned int y = 0;
	while(x < 2){
		x++;
		y++;
		if(x == y){
			ERROR: return 1;
		}
	}
	return 0;
}
