import { useState, useEffect } from 'react';

export default function useCities(country: string) {
  const [cities, setCities] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!country) {
      setCities([]);
      return;
    }

    const fetchCities = async () => {
      setLoading(true);
      try {
        // Try multiple free APIs for better city data
        let cities: string[] = [];

        // Method 1: Try CountriesNow.space API first (most reliable)
        try {
          const response = await fetch(`https://countriesnow.space/api/v0.1/countries/cities`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({ country: country })
          });
          
          if (response.ok) {
            const data = await response.json();
            if (data.data && Array.isArray(data.data)) {
              cities = data.data; // Get all cities, no limit
            }
          }
        } catch (error) {
          console.log('CountriesNow API failed, trying next method');
        }

        // Method 2: Try REST Countries with more detailed data
        if (cities.length === 0) {
          try {
            const countryResponse = await fetch(`https://restcountries.com/v3.1/name/${encodeURIComponent(country)}`);
            
            if (countryResponse.ok) {
              const countryData = await countryResponse.json();
              if (countryData.length > 0) {
                const countryInfo = countryData[0];
                // Get capital and major cities
                const capital = countryInfo.capital?.[0] || '';
                const majorCities = [
                  capital,
                  ...(countryInfo.altSpellings || []).slice(0, 3),
                  ...(countryInfo.translations?.eng?.common ? [countryInfo.translations.eng.common] : [])
                ].filter(Boolean);
                
                cities = [...new Set(majorCities)];
              }
            }
          } catch (error) {
            console.log('REST Countries API failed, trying next method');
          }
        }

        // Method 3: Try API Ninjas as last resort
        if (cities.length === 0) {
          try {
            const simpleResponse = await fetch(`https://api.api-ninjas.com/v1/city?country=${encodeURIComponent(country)}&limit=10`);
            
            if (simpleResponse.ok) {
              const simpleData = await simpleResponse.json();
              cities = simpleData.map((city: any) => city.name);
            }
          } catch (error) {
            console.log('API Ninjas failed, using fallback');
          }
        }

        // If all APIs fail, use fallback
        if (cities.length === 0) {
          cities = getFallbackCities(country);
        }

        setCities(cities);
      } catch (error) {
        console.error('Error fetching cities:', error);
        setCities(getFallbackCities(country));
      } finally {
        setLoading(false);
      }
    };

    fetchCities();
  }, [country]);

  return { cities, loading };
}

// Enhanced fallback cities with more cities per country
const getFallbackCities = (country: string): string[] => {
  const fallbackCities: { [key: string]: string[] } = {
    'United States': ['New York', 'Los Angeles', 'Chicago', 'Houston', 'Phoenix', 'Philadelphia', 'San Antonio', 'San Diego', 'Dallas', 'San Jose', 'Austin', 'Jacksonville', 'Fort Worth', 'Columbus', 'Charlotte'],
    'China': ['Shanghai', 'Beijing', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Tianjin', 'Chongqing', 'Nanjing', 'Wuhan', 'Xi\'an', 'Hangzhou', 'Dongguan', 'Foshan', 'Ningbo', 'Suzhou'],
    'Germany': ['Berlin', 'Hamburg', 'Munich', 'Cologne', 'Frankfurt', 'Stuttgart', 'Düsseldorf', 'Dortmund', 'Essen', 'Leipzig', 'Bremen', 'Dresden', 'Hannover', 'Nuremberg', 'Duisburg'],
    'Japan': ['Tokyo', 'Yokohama', 'Osaka', 'Nagoya', 'Sapporo', 'Fukuoka', 'Kobe', 'Kyoto', 'Kawasaki', 'Saitama', 'Hiroshima', 'Sendai', 'Chiba', 'Kitakyushu', 'Sakai'],
    'South Korea': ['Seoul', 'Busan', 'Incheon', 'Daegu', 'Daejeon', 'Gwangju', 'Suwon', 'Ulsan', 'Changwon', 'Seongnam', 'Bucheon', 'Ansan', 'Jeonju', 'Anyang', 'Pohang'],
    'India': ['Mumbai', 'Delhi', 'Bangalore', 'Hyderabad', 'Chennai', 'Kolkata', 'Pune', 'Ahmedabad', 'Surat', 'Jaipur', 'Lucknow', 'Kanpur', 'Nagpur', 'Indore', 'Thane'],
    'United Kingdom': ['London', 'Birmingham', 'Leeds', 'Glasgow', 'Sheffield', 'Bradford', 'Edinburgh', 'Liverpool', 'Manchester', 'Bristol', 'Cardiff', 'Coventry', 'Leicester', 'Belfast', 'Newcastle'],
    'France': ['Paris', 'Marseille', 'Lyon', 'Toulouse', 'Nice', 'Nantes', 'Strasbourg', 'Montpellier', 'Bordeaux', 'Lille', 'Rennes', 'Reims', 'Saint-Étienne', 'Toulon', 'Angers'],
    'Italy': ['Rome', 'Milan', 'Naples', 'Turin', 'Palermo', 'Genoa', 'Bologna', 'Florence', 'Bari', 'Catania', 'Venice', 'Verona', 'Messina', 'Padua', 'Trieste'],
    'Canada': ['Toronto', 'Montreal', 'Vancouver', 'Calgary', 'Edmonton', 'Ottawa', 'Winnipeg', 'Quebec City', 'Hamilton', 'Kitchener', 'London', 'Victoria', 'Halifax', 'Oshawa', 'Windsor'],
    'Vietnam': ['Ho Chi Minh City', 'Hanoi', 'Da Nang', 'Hai Phong', 'Can Tho', 'Bien Hoa', 'Hue', 'Nha Trang', 'Buon Ma Thuot', 'Vung Tau', 'Qui Nhon', 'Rach Gia', 'Long Xuyen', 'Thai Nguyen', 'Nam Dinh'],
    'Thailand': ['Bangkok', 'Chiang Mai', 'Pattaya', 'Phuket', 'Hat Yai', 'Udon Thani', 'Khon Kaen', 'Nakhon Ratchasima', 'Chiang Rai', 'Songkhla', 'Nakhon Si Thammarat', 'Ubon Ratchathani', 'Surat Thani', 'Nakhon Sawan', 'Lampang'],
    'Malaysia': ['Kuala Lumpur', 'George Town', 'Ipoh', 'Shah Alam', 'Johor Bahru', 'Melaka', 'Alor Setar', 'Miri', 'Petaling Jaya', 'Kuching', 'Kuantan', 'Sandakan', 'Tawau', 'Miri', 'Sibu'],
    'Singapore': ['Singapore'],
    'Indonesia': ['Jakarta', 'Surabaya', 'Bandung', 'Medan', 'Semarang', 'Palembang', 'Makassar', 'Tangerang', 'Depok', 'Bekasi', 'Bogor', 'Malang', 'Yogyakarta', 'Denpasar', 'Batam'],
  };
  
  return fallbackCities[country] || [];
}; 