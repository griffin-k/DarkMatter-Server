from django.shortcuts import render



def home(request):
    node_id = request.GET.get('id', None)
    return render(request, 'dashboard/home.html', {'node_id': node_id})

def devices(request):
    return render(request, 'dashboard/devices.html')