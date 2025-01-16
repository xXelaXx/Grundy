import matplotlib.pyplot as plt

x = [3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22]

yGrundyRecBrute = [1,2,3,7,18,19,39,112,113,227,1267,2559,6527,20141,62801,128101,422885,1433262,1433263,2866527]
yGrundyRecPerdantes = [1,2,3,5,14,15,17,40,41,43,143,187,399,673,1221,2139,4997,10176,10177,10179]
yGrundyRecPerdEtGagn = [1,2,3,5,11,12,14,29,30,32,79,101,146,185,272,330,472,609,610,612]
GrundyRecPerdantNeutre = [1,2,3,5,8,9,11,15,16,18,50,63,75,112,138,155,219,270,271,273]
GrundyRecGplusGequalsP = [1,2,3,5,8,9,11,15,16,18,23,29,32,38,50,53,59,77,78,80]

yGrundyRecBrute_Temps = [1566300,154300,83200,160100,108600,132300,143800,278600,205800,315400,737500,827900,2164100,6477100,18978300,39954200,33871600,108464400,122701800,204714800]
yGrundyRecPerdantes_Temps = [1414500,108000,86000,92900,137200,106300,128400,247900,231900,237800,836000,709700,468300,498800,958400,1352900,3254300,16053100,6381300,4637200]
yGrundyRecPerdEtGagn_Temps = [1570700,97500,92100,85200,183400,123300,192500,228000,252800,186000,323100,356500,675700,548400,548400,458500,559400,735100,917700,842700]
yGrundyRecPerdantNeutre_Temps = [2519900,275500,241500,456400,356700,314000,310400,238500,263300,412700,432000,465500,352200,622100,622400,772600,601100,654500,714100,568600]
yGrundyRecGplusGequalsP_Temps = [1712400,175200,102700,160900,108200,205000,157700,154600,186900,204100,178000,213300,194400,194700,177200,341500,186300,222400,328200,429300]


# Create a line plot with logarithmic scale
# plt.plot(x, yGrundyRecBrute, marker='o', label='GrundyRecBrute')
# plt.plot(x, yGrundyRecPerdantes, marker='o', label='GrundyRecPerdantes')
# plt.plot(x, yGrundyRecPerdEtGagn, marker='o', label='GrundyRecPerdEtGagn')
# plt.plot(x, GrundyRecPerdantNeutre, marker='o', label='GrundyRecPerdantNeutre')
# plt.plot(x, GrundyRecGplusGequalsP, marker='o', label='GrundyRecGplusGequalsP')

plt.plot(x, yGrundyRecBrute_Temps, marker='o', label='GrundyRecBrute')
plt.plot(x, yGrundyRecPerdantes_Temps, marker='o', label='GrundyRecPerdantes')
plt.plot(x, yGrundyRecPerdEtGagn_Temps, marker='o', label='GrundyRecPerdEtGagn')
plt.plot(x, yGrundyRecPerdantNeutre_Temps, marker='o', label='GrundyRecPerdantNeutre')
plt.plot(x, yGrundyRecGplusGequalsP_Temps, marker='o', label='GrundyRecGplusGequalsP')

# Set the y-axis to logarithmic scale
plt.yscale('log')

# Add legend
plt.legend()

# Add title and labels
plt.title('Jeu de Grundy (échelle logarithmique)')
# plt.xlabel('Compteur d\'opérations')	
plt.xlabel('Temps d\'éxécution (ns)')
plt.ylabel('Nbr Allumettes')

# Show the plot
plt.show()
