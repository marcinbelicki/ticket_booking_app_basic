cd ..
port=$(<portNumber)
clear

green=`tput setaf 2`
reset=`tput sgr0`

# setting date variables
day=$(date +%d)
month=$(date +%m)
year=$(date +%Y)
hour=$(date +%H)
minute=$(date +%M)


secondDate=$(LANG=en_us_88591;date -d "+1 days")
secondDay=$(date -d "$secondDate" +%d)
secondMonth=$(date -d "$secondDate" +%m)
secondYear=$(date -d "$secondDate" +%Y)
secondHour=$(date -d "$secondDate" +%H)
secondMinute=$(date -d "$secondDate" +%M)

echo "${green}1. The user selects the day and the time when he/she would like to see the movie.${reset}"
url="http://localhost:$port/timeinterval/firstdate/$year/$month/$day/$hour/$minute/lastdate/$secondYear/$secondMonth/$secondDay/$secondHour/$secondMinute"
echo "${green}2. The system lists movies available in the given time interval - title and screening times.${reset}"
screenings=$(curl $url)
echo "$screenings"

screeningLink=$(echo "$screenings" | egrep -o '/reserveScreening/[0-9]+' | sed -n '1 p')
echo "${green}3. The user chooses a particular screening.${reset}"
echo "${green}4. The system gives information regarding screening room and available seats.${reset}"
screening=$(curl -b cookies.txt --cookie-jar cookies.txt http://localhost:$port$screeningLink)
echo "$screening"
echo "${green}5. The user chooses seats...${reset}"
chosenSeats=$(echo "$screening" | egrep -o '/reserveSeat/[0-9]+/[0-9]+/[0-9]+' | sed -n '5,9 p')
echo "$chosenSeats" | while read -r seatLink; do curl -b cookies.txt --cookie-jar cookies.txt "http://localhost:$port$seatLink"; done


finalizeOrderLink="${screeningLink/reserveScreening/finalizeOrder}"
token=$(curl -b cookies.txt --cookie-jar cookies.txt "http://localhost:$port$finalizeOrderLink" | egrep -o 'name="csrfToken" value=".*')
csrf="${token/'name="csrfToken" value="'/}"
csrfToken="${csrf/'"/>'/}" 
echo $csrfToken
echo "${green}...and gives the name of the person doing the reservation (name and surname).${reset}"
echo "${green}6. The system gives back the total amount to pay and reservation expiration time.${reset}"
curl -b cookies.txt --cookie-jar cookies.txt -X POST "http://localhost:$port/ultimateFinalization" -H "Content-Type: application/x-www-form-urlencoded" -d "fname=Marcin&lname=Belicki&0=child&1=student&2=adult&3=child&4=student&csrfToken=$csrfToken"