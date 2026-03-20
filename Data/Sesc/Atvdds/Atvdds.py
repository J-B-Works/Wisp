import requests
import pandas as pd

todas_Atvdds = {} #dicionario inteligente

with open('Atvdds_urls.txt', 'r', encoding = 'utf-8') as urls: # utf-8 é pra não bugar os acentos em pt
    for linha in urls:
        url_limpa = linha.strip() #lida com os /n
        if url_limpa: #verifica se a linha n  ta vazia
            pingada = requests.get(url_limpa)

            #Verificação ---------------------------------------------
            if pingada.status_code == 200:
                raw_data = pingada.json()

                if 'atividade' in raw_data and raw_data['atividade']:
                    for item in raw_data['atividade']:

                        nome_atvdd = item.get('titulo','Sem título') #no get da pra fazer co case caso esteja vazio
                        raw_date_FS = item.get('dataPrimeiraSessao','Data não especificada')#Data da primeira sessão
                        raw_date_LS = item.get('dataUltimaSessao','')
                        clean_date_FS = raw_date_FS.replace('T',' ') if raw_date_FS else ' ' #substitui o separador de data e hora "T" por " "
                        clean_date_LS = raw_date_LS.replace('T',' ') if raw_date_FS else ' '

 #----------------------PAREI AQ -------------------------                       
                print("Dados extraídos com sucesso")
            else:
                print(f"Erro. Código de erro: ", pingada.status_code)
            #---------------------------------------------------------

            
            todas_Atvdds.update(nomes_categorias)#adiciona os nomes ao set
#----------------------------------------------------
#Output File
with open('Categorias.txt','w', encoding = 'utf-8') as Output:
    # utf-8 é pra não bugar os acentos em pt
    for nome in todas_Atvdds:
        Output.write(nome +'\n')