int main() {
  int i = 0;
  int k = 0;
  int m = 7;
  while (i<4) {
    i++;
    k--;
  }
  m = m * 2;
  goto CHECK;
  i--;
  CHECK:
  return 0;
}