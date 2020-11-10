#include <iostream>
#include <time.h>
#include <random>
#include <chrono>
using namespace std;

int main(int argc, char const *argv[])
{ /* code */
    
    time_t timer;
    time(&timer);

    minstd_rand g1;
    g1.seed(timer);

    

    for ( int i=0; i<100; i++ ) {
        int random_num = (g1() % 100) + 1;
        cout << random_num << endl;
        cout << g1() << endl;
        _sleep(1005);
    }

    []() { []() { []() { []() { []() {}; }; }; }; };
    system("pause");
    return 0;
}
